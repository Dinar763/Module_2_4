package service.impl;

import entity.Event;
import entity.EventType;
import entity.File;
import entity.User;
import exception.ServiceException;
import repository.EventRepository;
import repository.FileRepository;
import repository.UserRepository;
import repository.impl.EventRepositoryImpl;
import repository.impl.FileRepositoryImpl;
import repository.impl.UserRepositoryImpl;
import service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import static utill.HibernateUtil.*;

public class FileServiceImpl implements FileService {

    private static FileServiceImpl instance;
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private String basePath = "E:/fileStore";

    FileServiceImpl(FileRepository fileRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    private FileServiceImpl() {
        this(FileRepositoryImpl.getInstance(), UserRepositoryImpl.getInstance(), EventRepositoryImpl.getInstance());
    }

    public static synchronized FileServiceImpl getInstance() {
        if (instance == null) {
            instance = new FileServiceImpl();
        }
        return instance;
    }

    static void resetInstance() {
        instance = null;
    }

    @Override
    public File save(File file) {
        beginTransaction();
        try {
            File savedFile = fileRepository.save(file);
            commitTransaction();
            return savedFile;
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to save file: " + e);
        }
    }

    @Override
    public File update(File file) {
        beginTransaction();
        try {
            File updatedFile = fileRepository.update(file);
            commitTransaction();
            return updatedFile;
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to update file: " + e);
        }
    }

    @Override
    public File findByName(String fileName) {
        return fileRepository.findByName(fileName);
    }

    @Override
    public File findByFilePath(String filePath) {
        File findedFile = fileRepository.findByFilePath(filePath);
        return findedFile;
    }

    @Override
    public boolean existByName(String fileName) {
        return fileRepository.existByName(fileName);
    }

    @Override
    public void deleteById(String idParam) {
        Long id = Long.parseLong(idParam);
        File file = fileRepository.findById(id);
        if (file == null) {
            throw new ServiceException("File not found");
        }
        deleteFileWithEvent(file);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new ServiceException("ID must be not null");
        }
        File file = fileRepository.findById(id);
        if (file == null) {
            throw new ServiceException("File not found");
        }
        deleteFileWithEvent(file);
    }


    @Override
    public void deleteByName(String fileName) {
        File file = fileRepository.findByName(fileName);
        if (file == null) {
            throw new ServiceException("File not found");
        }
        deleteFileWithEvent(file);
    }

    @Override
    public File findById(Long id) {
        if (id == null) {
            throw new ServiceException("ID must be not null");
        }
        File findedFile = fileRepository.findById(id);
        return findedFile;
    }

    @Override
    public List<File> findAll() {
        List<File> files = fileRepository.findAll();
        return files != null ? files : Collections.emptyList();
    }

    @Override
    public File uploadFile(String fileName, InputStream fileContent, Long userId) throws IOException {
        beginTransaction();
        try {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            if (fileRepository.existByName(fileName)) {
                throw new IllegalArgumentException("File with name '" + fileName + "' already exists");
            }
            var filePath = saveFileToDisk(fileName, fileContent);

            File fileEntity = File.builder()
                                  .name(fileName)
                                  .filePath(filePath)
                                  .user(user)
                                  .build();

            File savedFile = fileRepository.save(fileEntity);


            Event eventEntity = Event.builder()
                                     .user(user)
                                     .fileId(savedFile.getId())
                                     .fileName(savedFile.getName())
                                     .filePath(savedFile.getFilePath())
                                     .type(EventType.UPLOAD)
                                     .build();
            eventRepository.save(eventEntity);
            commitTransaction();
            return savedFile;
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }


    private String saveFileToDisk(String fileName, InputStream fileContent) throws IOException {
        var fileFullPath = Path.of(basePath, fileName);

        Files.createDirectories(fileFullPath.getParent());

        Files.copy(fileContent, fileFullPath,
                StandardCopyOption.REPLACE_EXISTING);


        return fileFullPath.toString();
    }

    private void deleteFileWithEvent(File file) {
        beginTransaction();
        try {
            User user = file.getUser();

            Event eventEntity = Event.builder()
                                     .user(user)
                                     .fileId(file.getId())
                                     .fileName(file.getName())
                                     .filePath(file.getFilePath())
                                     .type(EventType.DELETE)
                                     .build();
            eventRepository.save(eventEntity);
            fileRepository.deleteById(file.getId());
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw new ServiceException("Failed to delete file: " + e.getMessage());
        }
    }
}
