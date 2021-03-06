package edu.depaul.taskmanager.api.repository;

import edu.depaul.taskmanager.api.model.TaskList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskListRepository extends MongoRepository<TaskList, String> {
    List<TaskList> findByOwnerId(String ownerId);
    List<TaskList> findByOwnerIdAndListTypeEquals(String ownerId, String listType);
}
