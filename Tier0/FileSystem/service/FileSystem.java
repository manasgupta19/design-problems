package Tier0.FileSystem.service;

import java.util.List;

public interface FileSystem {
    void mkdir(String path);
    void writeFile(String path, String content);
    String readFile(String path);
    List<String> ls(String path);
}
