package repository;

import entity.File;

public interface FileRepository extends GenericRepository<File, Long> {

    File findByName(String fileName);
    File findByFilePath(String filePath);
    boolean existByName(String fileName);
    void deleteByName(String fileName);
}
