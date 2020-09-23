package com.eveino;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 通用密码修改，配置文件生成，初始化脚本生成
 *
 * @author 贰拾壹
 * @create 2020-05-21 17:45
 */

public class GenPass {

    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("usage:");
            System.out.println("java -cp GenPass.jar com.eveino.GenPass");
            System.exit(0);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String rabbitMQPass = "vpUNx2TpULV1kB7l";
        String mySQLPass = "8TAQRc9EOkV607qm";
        String redisPass = "snclGVwsAywx1G2R";
        String eurekaPass = "xJjbahN2c1hOgV5d";
        String druidMonitorPass = "9L4GH0XagHTR8HqS";
        String jwtKey = "DVi2y9KGJy3uz57AaWmY7FJH";
        String adminPass = "$2a$10$7wmPIhsnZS3/I1xrQQOtvep9J/GVt2ofofkF4365cAxoFP8E5Zjd6";

        String newRabbitMQPass = generatePassword(16);
        String newMySQLPass = generatePassword(16);
        String newRedisPass = generatePassword(16);
        String newEurekaPass = generatePassword(16);
        String newDruidMonitorPass = generatePassword(16);
        String newJwtKey = generatePassword(32);
        String newAdminPass = generatePassword(24);
        String encodeNewAdminPass = bCryptPasswordEncoder.encode(newAdminPass);

        String pathname = "." + File.separator + "defaultConf";
        File file = new File(pathname);
        String[] list = file.list();
        String newPath = file.getParent() + File.separator;
        File newPassTxt = new File(newPath + "newPass.txt");
        if (newPassTxt.exists()) {
            System.out.println("newPass.txt已存在，如需重新生成，请将其删除后重试");
            System.out.println("如已部署到生产环境，请备份newPass.txt");
            System.exit(0);

        }
        for (String rawName : list) {
            String tempConfigName = newPath + File.separator + "MagiCude" + File.separator + rawName;
            if ("magicude.sql".equals(rawName)) {
                tempConfigName = newPath + File.separator + "MagiCude" + File.separator + "db" + File.separator + rawName;
            }
            File newConfig = new File(tempConfigName);
            try {

                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(pathname + File.separator + rawName)), StandardCharsets.UTF_8));
                     BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newConfig), StandardCharsets.UTF_8));
                     BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(newPassTxt))
                ) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        line = getString(line, rabbitMQPass, newRabbitMQPass);
                        line = getString(line, mySQLPass, newMySQLPass);
                        line = getString(line, redisPass, newRedisPass);
                        line = getString(line, eurekaPass, newEurekaPass);
                        line = getString(line, druidMonitorPass, newDruidMonitorPass);
                        line = getString(line, jwtKey, newJwtKey);
                        line = getString(line, adminPass, encodeNewAdminPass);
                        bufferedWriter.write(line);
                        bufferedWriter.write("\n");
                        bufferedWriter.flush();
                    }

                    bufferedWriter2.write("Rabbit:\t" + newRabbitMQPass + "\r\n");
                    bufferedWriter2.write("MySQL:\t" + newMySQLPass + "\r\n");
                    bufferedWriter2.write("Redis:\t" + newRedisPass + "\r\n");
                    bufferedWriter2.write("Eureka:\t" + newEurekaPass + "\r\n");
                    bufferedWriter2.write("DruidM:\t" + newDruidMonitorPass + "\r\n");
                    bufferedWriter2.write("JwtKey:\t" + newJwtKey + "\r\n");
                    bufferedWriter2.write("admin:\t" + newAdminPass + "\n");
                    bufferedWriter2.flush();

                }
            } catch (IOException ignored) {
            }
        }
        System.out.println("请备份newPass.txt");
    }

    private static String getString(String line, String oldPass, String newPass) {
        if (line.contains(oldPass)) {
            line = line.replace(oldPass, newPass);
        }
        return line;
    }

    public static String generatePassword(int length) {
        StringBuilder password = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        String oldString = "";
        for (int i = 0; i < length; i++) {
            int flag = secureRandom.nextInt(2);
            //字母
            if (flag == 1) {
                //0小写，1大写
                int temp = secureRandom.nextInt(2) == 1 ? 65 : 97;
                password.append((char) (secureRandom.nextInt(26) + temp));
            } else {
                //数字
                password.append(secureRandom.nextInt(10));
            }
            String newString = password.substring(password.length() - 1, password.length());
            //相邻两位不能相同
            if (oldString.equals(newString)) {
                int temp = secureRandom.nextInt(2) == 1 ? 65 : 97;
                password.deleteCharAt(password.length() - 1);
                password.append((char) (secureRandom.nextInt(26) + temp));
            }
            oldString = newString;

        }
        return password.toString();
    }


}
