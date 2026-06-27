package com.example.labmanagement.config;

import com.example.labmanagement.entity.Category;
import com.example.labmanagement.entity.Equipment;
import com.example.labmanagement.entity.EquipmentStatus;
import com.example.labmanagement.entity.Role;
import com.example.labmanagement.entity.UserAccount;
import com.example.labmanagement.entity.UserStatus;
import com.example.labmanagement.repository.CategoryRepository;
import com.example.labmanagement.repository.EquipmentRepository;
import com.example.labmanagement.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserAccountRepository userRepository,
            CategoryRepository categoryRepository,
            EquipmentRepository equipmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.equipmentRepository = equipmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        migrateDefaultUsernames();
        seedUsers();
        seedCategoriesAndEquipment();
    }

    private void migrateDefaultUsernames() {
        migrateDefaultUser("admin", "admin520", "admin1314");
        migrateDefaultUser("student", "user520", "user1314");
        refreshDefaultPassword("admin520", "admin1314");
        refreshDefaultPassword("user520", "user1314");
    }

    private void migrateDefaultUser(String oldUsername, String newUsername, String rawPassword) {
        if (userRepository.findByUsername(newUsername).isPresent()) {
            return;
        }
        userRepository.findByUsername(oldUsername).ifPresent(user -> {
            user.setUsername(newUsername);
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        });
    }

    private void refreshDefaultPassword(String username, String rawPassword) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
        });
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }

        UserAccount admin = new UserAccount();
        admin.setUsername("admin520");
        admin.setPassword(passwordEncoder.encode("admin1314"));
        admin.setRealName("系统管理员");
        admin.setRole(Role.ADMIN);
        admin.setPhone("13800000000");
        admin.setStatus(UserStatus.ENABLED);
        userRepository.save(admin);

        UserAccount student = new UserAccount();
        student.setUsername("user520");
        student.setPassword(passwordEncoder.encode("user1314"));
        student.setRealName("学生用户");
        student.setRole(Role.USER);
        student.setPhone("13900000000");
        student.setStatus(UserStatus.ENABLED);
        userRepository.save(student);
    }

    private void seedCategoriesAndEquipment() {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category computer = category("计算机设备", "实验室教学与开发用计算机设备");
        Category camera = category("摄像设备", "实验记录、图像采集相关设备");
        Category robot = category("机器人设备", "机器人课程实验设备");
        Category network = category("网络设备", "交换机、路由器等网络实验设备");

        equipment("实验笔记本", "EQ-COM-001", computer, "ThinkBook 14", 10, 8, "A101", "Java课程实验");
        equipment("高清摄像机", "EQ-CAM-001", camera, "Sony AX45", 4, 4, "B203", "图像采集实验");
        equipment("机器人套件", "EQ-ROB-001", robot, "EduBot V2", 6, 5, "C305", "机器人控制实验");
        equipment("千兆交换机", "EQ-NET-001", network, "24口", 3, 3, "A205", "网络组网实验");
    }

    private Category category(String name, String description) {
        Category category = new Category();
        category.setCategoryName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private void equipment(
            String name,
            String code,
            Category category,
            String model,
            int total,
            int available,
            String location,
            String remark
    ) {
        Equipment equipment = new Equipment();
        equipment.setEquipmentName(name);
        equipment.setEquipmentCode(code);
        equipment.setCategory(category);
        equipment.setModel(model);
        equipment.setTotalQuantity(total);
        equipment.setAvailableQuantity(available);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setLocation(location);
        equipment.setRemark(remark);
        equipmentRepository.save(equipment);
    }
}
