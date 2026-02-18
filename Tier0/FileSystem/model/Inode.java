package Tier0.FileSystem.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inode {
    int id;
    boolean isDirectory;
    int size; // Total bytes for file
    List<Integer> blockIndices; // Direct pointers to data blocks
    Map<String, Integer> directoryMap; // Name -> InodeID mapping

    public Inode(int id, boolean isDirectory) {
        this.id = id;
        this.isDirectory = isDirectory;
        this.size = 0;
        this.blockIndices = new ArrayList<>();
        this.directoryMap = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public int getSize() {
        return size;
    }

    public List<Integer> getBlockIndices() {
        return blockIndices;
    }

    public Map<String, Integer> getDirectoryMap() {
        return directoryMap;
    }

    public void setSize(int length) {
        this.size = length;
    }

    public void setBlockIndices(List<Integer> allocatedBlocks) {
        this.blockIndices = allocatedBlocks;
    }
}
