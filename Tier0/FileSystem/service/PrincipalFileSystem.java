package Tier0.FileSystem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tier0.FileSystem.model.Inode;

public class PrincipalFileSystem implements FileSystem {

    // Configuration
    private static final int BLOCK_SIZE = 4; // Tiny block size to force fragmentation logic
    private static final int MAX_BLOCKS = 100;

    // Physical Storage
    private final byte[][] dataBlocks; // The "Disk"
    private final boolean[] blockBitmap; // Tracks free blocks

    // Metadata Storage
    private final Map<Integer, Inode> inodeTable;
    private int inodeCounter = 0;
    private static final int ROOT_INODE_ID = 0;

    public PrincipalFileSystem() {
        this.dataBlocks = new byte[MAX_BLOCKS][BLOCK_SIZE];
        this.blockBitmap = new boolean[MAX_BLOCKS]; // false = free, true = occupied
        this.inodeTable = new HashMap<>();

        // Initialize Root Directory
        Inode root = new Inode(ROOT_INODE_ID, true);
        inodeTable.put(ROOT_INODE_ID, root);
        inodeCounter++;
    }

    // ---------------------------------------------------------
    // 2. PATH RESOLUTION (The Traversal Logic)
    // ---------------------------------------------------------
    private Inode resolvePath(String path) {
        if (path.equals("/")) return inodeTable.get(ROOT_INODE_ID);

        String[] parts = path.split("/");
        Inode current = inodeTable.get(ROOT_INODE_ID);

        for (String part : parts) {
            if (part.isEmpty()) continue;

            if (!current.isDirectory()) throw new RuntimeException("Traversal failed: " + part + " is not a directory.");
            if (!current.getDirectoryMap().containsKey(part)) return null; // Not found

            int nextId = current.getDirectoryMap().get(part);
            current = inodeTable.get(nextId);
        }
        return current;
    }

    // ---------------------------------------------------------
    // 3. FILE OPERATIONS
    // ---------------------------------------------------------
    @Override
    public void mkdir(String path) {
        // Simple logic: assume parent exists for this snippet (e.g., /usr/bin -> /usr must exist)
        int lastSlash = path.lastIndexOf('/');
        String parentPath = lastSlash == 0 ? "/" : path.substring(0, lastSlash);
        String dirName = path.substring(lastSlash + 1);

        Inode parent = resolvePath(parentPath);
        if (parent == null) throw new RuntimeException("Parent path does not exist");
        if (parent.getDirectoryMap().containsKey(dirName)) throw new RuntimeException("Directory exists");

        // Allocate Inode
        int newId = inodeCounter++;
        Inode newDir = new Inode(newId, true);
        inodeTable.put(newId, newDir);

        // Link to Parent
        parent.getDirectoryMap().put(dirName, newId);
    }

    @Override
    public void writeFile(String path, String data) {
        int lastSlash = path.lastIndexOf('/');
        String parentPath = lastSlash == 0 ? "/" : path.substring(0, lastSlash);
        String fileName = path.substring(lastSlash + 1);

        Inode parent = resolvePath(parentPath);
        if (parent == null) throw new RuntimeException("Path not found");

        // Create or Get Inode
        Inode fileInode;
        if (parent.getDirectoryMap().containsKey(fileName)) {
            fileInode = inodeTable.get(parent.getDirectoryMap().get(fileName));
            freeBlocks(fileInode); // Overwrite mode: clear old blocks
        } else {
            int newId = inodeCounter++;
            fileInode = new Inode(newId, false);
            inodeTable.put(newId, fileInode);
            parent.getDirectoryMap().put(fileName, newId);
        }

        // Allocate Blocks [Source 1177]
        byte[] bytes = data.getBytes();
        fileInode.setSize(bytes.length);
        int blocksNeeded = (int) Math.ceil((double) bytes.length / BLOCK_SIZE);

        List<Integer> allocatedBlocks = allocateBlocks(blocksNeeded);
        fileInode.setBlockIndices(allocatedBlocks);

        // Copy Data
        int byteOffset = 0;
        for (int blockId : allocatedBlocks) {
            int length = Math.min(BLOCK_SIZE, bytes.length - byteOffset);
            System.arraycopy(bytes, byteOffset, dataBlocks[blockId], 0, length);
            byteOffset += length;
        }
    }

    @Override
    public String readFile(String path) {
        Inode inode = resolvePath(path);
        if (inode == null || inode.isDirectory()) throw new RuntimeException("File not found or is directory");

        StringBuilder sb = new StringBuilder();
        int bytesRead = 0;

        for (int blockId : inode.getBlockIndices()) {
            // Determine how many bytes to read from this block (might be partial at end)
            int length = Math.min(BLOCK_SIZE, inode.getSize() - bytesRead);
            sb.append(new String(dataBlocks[blockId], 0, length));
            bytesRead += length;
        }
        return sb.toString();
    }

    // ---------------------------------------------------------
    // 4. BLOCK MANAGER (Allocation Strategy)
    // ---------------------------------------------------------
    private List<Integer> allocateBlocks(int count) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < MAX_BLOCKS && result.size() < count; i++) {
            if (!blockBitmap[i]) {
                blockBitmap[i] = true; // Mark used
                result.add(i);
            }
        }
        if (result.size() < count) throw new RuntimeException("Disk Full");
        return result;
    }

    private void freeBlocks(Inode inode) {
        for (int blockId : inode.getBlockIndices()) {
            blockBitmap[blockId] = false;
        }
        inode.setBlockIndices(new ArrayList<>());
    }

    @Override
    public List<String> ls(String path) {
        Inode inode = resolvePath(path);

        // 1. Fail Fast: Path existence check
        if (inode == null) {
            throw new RuntimeException("Path not found: " + path);
        }

        // 2. Handle Directory: Return children
        if (inode.isDirectory()) {
            // Principal Engineer Trait: Determinism.
            // We sort the keys so the API response is predictable/testable.
            List<String> listing = new ArrayList<>(inode.getDirectoryMap().keySet());
            Collections.sort(listing);
            return listing;
        }  

        // 3. Handle File: Return the filename itself
        // (Mimics standard 'ls' behavior when pointing to a file)
        else {
            String fileName = path.contains("/")
                ? path.substring(path.lastIndexOf('/') + 1)
                : path;
            return Collections.singletonList(fileName);
        }
    }

}
