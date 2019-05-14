package edu.depaul.taskmanager.api.controller;

import edu.depaul.taskmanager.api.model.TaskList;
import edu.depaul.taskmanager.api.service.TaskListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class ListController {

    private TaskListService taskListService;

    public ListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @PostMapping("/users/{userId}/lists")
    public ResponseEntity<TaskList> createPersonalList(@PathVariable String userId, @RequestBody String listName, UriComponentsBuilder uriComponentsBuilder) {
        TaskList list = taskListService.createPersonalList(userId, listName);
        return ResponseEntity
                .created(uriComponentsBuilder.path("/users/{userId}/lists/{listId}").buildAndExpand(userId, list.getId()).toUri())
                .body(list);
    }
}
