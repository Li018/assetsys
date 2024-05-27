package com.example.assetsys.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

/**
 * @program: assetsys
 * @ClassName:SystemInformation
 * @description: 系统参数类
 * @author:li
 * @Version 3.0
 **/
@Getter
@Setter

public class SystemInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    private double freeDiskSpaceGB;
    private double maxMemoryGB;
    private double freeMemoryGB;
    private double totalDiskSpaceGB;
    private String osName;  // 操作系统名称
    private String osArch;  // 操作系统架构
    private String osVersion;  // 操作系统版本
    private String userDir;//当前程序所在目录
    private String javaVersion;  // Java版本
    private String javaVendor;  // Java供应商
    private String javaVmName;  // Java虚拟机名称
    private String javaVenDorUrl;//供应商官网
    private long maxMemory;  // 最大内存
    private long totalMemory;  //总内存
    private Double totalMemoryGB;// 总内存GB
    private long freeMemory;  // 空闲内存
    private long availableProcessors;  // 可用处理器数量
    private long systemLoadAverage; // 系统负载平均值（如果支持的话）
    private long freeDiskSpace; // 磁盘的剩余空间
    private long totalDiskSpace; // 磁盘的总空间
    private String userLanguage; // 用户语言
    private String fileSeparator; // 文件分隔符
    private String lineSeparator; // 行分隔符

    // 构造方法
    public SystemInformation() {
        this.osName = System.getProperty("os.name");
        this.osArch = System.getProperty("os.arch");
        this.osVersion = System.getProperty("os.version");
        this.javaVersion = System.getProperty("java.version");
        this.javaVendor = System.getProperty("java.vendor");
        this.javaVmName = System.getProperty("java.vm.name");
        this.javaVenDorUrl = System.getProperty("java.vendor.url");
        this.userDir = System.getProperty("user.dir");
        Runtime runtime = Runtime.getRuntime();
        this.maxMemory = runtime.maxMemory();
        this.totalMemory = runtime.totalMemory();
        this.freeMemory = runtime.freeMemory();
        this.userLanguage = System.getProperty("user.language");
        this.fileSeparator = System.getProperty("file.separator");
        this.lineSeparator = System.getProperty("line.separator");
        // 转换为GB
        // 使用 DecimalFormat 保留两位小数
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        this.maxMemoryGB = Double.parseDouble(decimalFormat.format((double) this.maxMemory / (1024 * 1024 * 1024)));

        this.freeMemoryGB = Double.parseDouble(decimalFormat.format((double) this.freeMemory / (1024 * 1024 * 1024)));
        this.availableProcessors = runtime.availableProcessors();

        // 获取系统负载平均值（如果支持的话）
        try {
            this.systemLoadAverage = (long) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        } catch (UnsupportedOperationException e) {
            this.systemLoadAverage = -1; // 不支持的情况下设置为-1
        }

        // 获取磁盘信息
        File file = new File("/");
        this.totalDiskSpace = file.getTotalSpace();
        this.freeDiskSpace = file.getFreeSpace();
        this.totalDiskSpaceGB = Double.parseDouble(decimalFormat.format(this.totalDiskSpace / (1024.0 * 1024 * 1024)));
        this.freeDiskSpaceGB = Double.parseDouble(decimalFormat.format(this.freeDiskSpace / (1024.0 * 1024 * 1024)));
    }

    // 添加 getter 方法

    // ...
}
