package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MyClass {

    private static final int PROJECT_FRAMEWORK = 1;
    private static final int DAGGER2MVP = 2;
    private static final int ACTIVITY = 1;
    private static final int FRAGMENT = 2;

    private static final boolean isDebug = false;

    public static void main(String args[]) {
        String hint = "请输入数字选择：1.项目框架生成 2.Dagger2 MVP模块生成";
        System.out.println(hint);
        Scanner in = new Scanner(System.in);
        int mode = Integer.valueOf(in.next());
        do {
            if (mode == PROJECT_FRAMEWORK || mode == DAGGER2MVP) {
            } else {
                System.out.println("输入格式不正确!");
                System.out.println(hint);
                mode = Integer.valueOf(in.next());
            }
        } while (!(mode == PROJECT_FRAMEWORK ||mode == DAGGER2MVP));
        String filePath = null;
        String buildPath = null;
//        String resourcesPath = null;
        String rootPath = null;
        if (isDebug) {
        } else {
//            D:/Android/workspace/MyAndroidMVPTemplates/build/classes/java/main
            filePath = MyClass.class.getClassLoader().getResource("").getPath();
            filePath = filePath.substring(1, filePath.length() - 1);
//            D:/Android/workspace/MyAndroidMVPTemplates/build
            buildPath = filePath.substring(0, filePath.indexOf("build") + 5);
            rootPath = filePath.substring(0, filePath.indexOf("build") - 1);
//            resourcesPath = buildPath + "/resources/main";
        }
        if (mode == PROJECT_FRAMEWORK) {
            generateProjectFrameworkTemplates(in, buildPath, rootPath + "/src/main/resources");
        }
        if (mode == DAGGER2MVP) {
            System.out.println("请输入数字选择：1.生成Activity 2.生成Fragment");
            int type = Integer.valueOf(in.next());
            do {
                if (type == ACTIVITY || type == FRAGMENT) {
                } else {
                    System.out.println("输入格式不正确!");
                    System.out.println(hint);
                    type = Integer.valueOf(in.next());
                }
            } while (!(type == ACTIVITY ||type == FRAGMENT));
            generateDagger2MVPTemplates(in, buildPath, rootPath, type);
        }

    }

    /**
     * 项目框架生成
     * @param in
     * @param buildPath
     * @param rootPath
     */
    private static void generateProjectFrameworkTemplates(Scanner in, String buildPath, String rootPath) {
        System.out.println("请输入项目名称和包名(格式:项目名,包名)：");
        String[] reads = in.next().split(",");
        do {
            if (reads.length != 2) {
                System.out.println("输入格式不正确!请重新输入(格式:项目名,包名)：");
                reads = in.next().split(",");
            }
        } while (reads.length != 2);
        String templatesFileName = "/project-templates/" + reads[0];
        System.out.println("开始复制项目框架...");
        String myProjectFrameworkName = "MyProjectFramework";
        try {
            copyProjectDirectory(rootPath + "/" + myProjectFrameworkName, buildPath + templatesFileName, reads);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("模项目框架生成成功,保存在build" + templatesFileName + "/");
    }

    /**
     * Dagger2 MVP模块生成
     * @param in
     * @param buildPath
     * @param resourcesPath
     * @param type
     */
    private static void generateDagger2MVPTemplates(Scanner in, String buildPath, String resourcesPath, int type) {
        System.out.println("请输入MVP模块名称和包名(格式:模块名,包名)：");
        String[] reads = in.next().split(",");
        do {
            if (reads.length != 2) {
                System.out.println("输入格式不正确!请重新输入(格式:模块名,包名)：");
                reads = in.next().split(",");
            }
        } while (reads.length != 2);
        String templatesFileName = "/dagger2-mvp-templates";
        System.out.println("开始复制模板...");
//        //生产模块目录
		String dagger2Name;
        if (type == ACTIVITY) {
            dagger2Name = "dagger2";
        } else {
            dagger2Name = "fragment_dagger2";
            reads[0] = "Fragment" + reads[0];
        }
        String[] modules = reads[0].split("(?<!^)(?=[A-Z])");
        String moduleName = "";
        for (int i = 0; i < modules.length; i++) {
            modules[i] = modules[i].toLowerCase();
            if (i == modules.length - 1) {
                moduleName += modules[i];
            } else {
                moduleName += modules[i] + "_";
            }
        }        
        try {
            copyModuleDirectory(resourcesPath + "/" + dagger2Name,
                    buildPath + templatesFileName + "/" + moduleName, reads, moduleName);
            (new File(buildPath + templatesFileName + "/" + moduleName + "/model")).mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("模板生成成功,保存在build" + templatesFileName + "/" + moduleName);
    }

    /**
     * 复制项目目录
     * @param sourcePath
     * @param newPath
     * @param reads
     * @throws Exception
     */
    private static void copyProjectDirectory(String sourcePath, String newPath,
                                             String[] reads) throws Exception {
        File file = new File(sourcePath);
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdirs();
        }
        boolean isNewPackageDirectory = false;
        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
                String directory = filePath[i];
                if (directory.equals("java")) {
                    String[] packages = reads[1].split("\\.");
                    for (String aPackage : packages) {
                        directory += file.separator + aPackage;
                    }
                    isNewPackageDirectory = true;
                }
                copyProjectDirectory(sourcePath  + file.separator  + filePath[i],
                        newPath  + file.separator + directory, reads);
            }
            if (new File(sourcePath  + file.separator + filePath[i]).isFile()) {
                String directory = filePath[i];
                if (isNewPackageDirectory) {
                    String[] packages = reads[1].split("\\.");
                    for (String aPackage : packages) {
                        directory += file.separator + aPackage;
                    }
                }
                copyProjectFile(new File(sourcePath + file.separator + filePath[i]),
                        newPath + file.separator + directory, reads);
            }

        }
    }

    /**
     * 复制模块目录
     * @param sourcePath
     * @param newPath
     * @param reads
     * @param moduleName
     * @throws Exception
     */
    private static void copyModuleDirectory(String sourcePath, String newPath, String[] reads, String moduleName) throws Exception {
        File file = new File(sourcePath);
        String[] filePath = file.list();

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdirs();
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + file.separator + filePath[i])).isDirectory()) {
                copyModuleDirectory(sourcePath  + file.separator  + filePath[i],
                        newPath  + file.separator + filePath[i], reads, moduleName);
            }

            if (new File(sourcePath  + file.separator + filePath[i]).isFile()) {
                String newFileName = file.separator + filePath[i].replace("Dagger2", reads[0]);
                copyModuleFile(new File(sourcePath + file.separator + filePath[i]),
                        newPath + newFileName, reads, moduleName);
            }
        }
    }


    /**
     * 复制模块文件
     * @param sourceFile
     * @param targetPath
     * @param reads
     * @param moduleName
     */
    private static void copyModuleFile(File sourceFile, String targetPath, String[] reads, String moduleName) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (new FileInputStream(sourceFile)));
        StringBuilder stringBuffer = new StringBuilder();
        //逐行查找替换内容
        for (String temp; (temp = bufferedReader.readLine()) != null; ) {
            if (temp.contains("$Name")) {
                temp = temp.replace("$Name", reads[0]);
            }
            if (temp.contains("$LowerNameCase")) {
                temp = temp.replace("$LowerNameCase", toLowerCaseFirstOne(reads[0]));
            }
            if (temp.contains("$Package")) {
                temp = temp.replace("$Package", reads[1]);
            }
            if (temp.contains("$ModuleName")) {
                temp = temp.replace("$ModuleName", moduleName);
            }
            if (temp.contains("Created by Vincent")) {
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                temp = temp.replace("$Time", formatter.format(currentTime));
            }
            stringBuffer.append(temp);
            stringBuffer.append(System.getProperty("line.separator"));
        }
        bufferedReader.close();
        //替换后输出的文件位置
        PrintWriter printWriter = new PrintWriter(targetPath);
        printWriter.write(stringBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }

    /**
     * 复制项目文件
     * @param sourceFile
     * @param targetPath
     * @param reads
     * @throws Exception
     */
    private static void copyProjectFile(File sourceFile, String targetPath, String[] reads) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (new FileInputStream(sourceFile)));
        StringBuilder stringBuffer = new StringBuilder();
        //逐行查找替换内容
        for (String temp; (temp = bufferedReader.readLine()) != null; ) {
            if (temp.contains("$Project")) {
                temp = temp.replace("$Project", reads[0]);
            }
            if (temp.contains("$Package")) {
                temp = temp.replace("$Package", reads[1]);
            }
            if (temp.contains("Created by Vincent")) {
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                temp = temp.replace("$Time", formatter.format(currentTime));
            }
            stringBuffer.append(temp);
            stringBuffer.append(System.getProperty("line.separator"));
        }
        bufferedReader.close();
        //替换后输出的文件位置
        PrintWriter printWriter = new PrintWriter(targetPath);
        printWriter.write(stringBuffer.toString().toCharArray());
        printWriter.flush();
        printWriter.close();
    }

    /**
     * 首字母转小写
     * @param s
     * @return
     */
    private static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

}
