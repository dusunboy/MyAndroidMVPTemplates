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

    public static void main(String args[]) {
        System.out.println("请输入MVP模块名称和包名(格式:模块名,包名)：");
        Scanner in = new Scanner(System.in);
        String[] reads = in.next().split(",");
        do {
            if (reads.length != 2) {
                System.out.println("输入格式不正确!请重新输入(格式:模块名,包名)：");
                reads = in.next().split(",");
            }
        } while (reads.length != 2);
        try {
            String filePath = MyClass.class.getClassLoader().getResource("").getPath();
            filePath = filePath.substring(1, filePath.length() - 1);
            String buildPath = filePath.substring(0, filePath.indexOf("build") + 5);
            String resourcesPath = buildPath + "/resources/main";
            File mvpTemplatesFile = new File(buildPath + "/mvp-templates");
            if (!mvpTemplatesFile.exists()) {
                mvpTemplatesFile.mkdirs();
            }
            System.out.println("开始复制模板...");
            //生产模块目录
            File moduleFile = new File(mvpTemplatesFile + "/" + reads[0]);
            if (!moduleFile.exists()) {
                moduleFile.mkdirs();
                //生成view
                File viewFile = new File(moduleFile.getAbsolutePath() + "/view");
                if (!viewFile.exists()) {
                    viewFile.mkdirs();
                }
                //生成presenter
                File presenterFile = new File(moduleFile.getAbsolutePath() + "/presenter");
                if (!presenterFile.exists()) {
                    presenterFile.mkdirs();
                }
                //生成model
                File modelFile = new File(moduleFile.getAbsolutePath() + "/model");
                if (!modelFile.exists()) {
                    modelFile.mkdirs();
                }
            }
            File demoActivityFile = new File(resourcesPath + "/demo/DemoActivity.java");
            copyFile(demoActivityFile,
                    mvpTemplatesFile.getAbsolutePath() + "/" + reads[0] + "/" + reads[0] + "Activity.java",
                    reads);
            File demoViewFile = new File(resourcesPath + "/demo/view/DemoView.java");
            copyFile(demoViewFile,
                    mvpTemplatesFile.getAbsolutePath() + "/" + reads[0] + "/view/" + reads[0] + "View.java",
                    reads);
            File demoPresenterFile = new File(resourcesPath + "/demo/presenter/DemoPresenter.java");
            copyFile(demoPresenterFile,
                    mvpTemplatesFile.getAbsolutePath() + "/" + reads[0] + "/presenter/" + reads[0] + "Presenter.java",
                    reads);
            File demoPresenterImplFile = new File(resourcesPath + "/demo/presenter/DemoPresenterImpl.java");
            copyFile(demoPresenterImplFile,
                    mvpTemplatesFile.getAbsolutePath() + "/" + reads[0] + "/presenter/" + reads[0] + "PresenterImpl.java",
                    reads);
            System.out.println("模板生成成功,保存在build/mvp-templates/" + reads[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件并且替换文件里的内容
     *
     * @param sourceFile
     * @param targetPath
     * @param reads
     */
    private static void copyFile(File sourceFile, String targetPath, String[] reads) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                (new FileInputStream(sourceFile)));
        StringBuilder stringBuffer = new StringBuilder();
        //逐行查找替换内容
        for (String temp; (temp = bufferedReader.readLine()) != null; temp = null) {
            if (temp.contains("$Name")) {
                temp = temp.replace("$Name", reads[0]);
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

}
