package edu.depaul.taskmanager.api.service;

import edu.depaul.taskmanager.api.model.Task;
import edu.depaul.taskmanager.api.model.TaskList;
import edu.depaul.taskmanager.api.repository.TaskListRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskListServiceTest {

    private TaskListService service;
    private TaskListRepository repository;

    private String userId = "1234";
    private String listId = "5678";
    private String listName = "To Do List";
    private TaskList taskList = TaskList.newBuilder().withId(listId).withName(listName).withOwnerId(userId).build();
    private TaskList anotherTaskList = TaskList.newBuilder().withId("9999").withName("Another List").withOwnerId(userId).build();
    private Task task1 = Task.newBuilder().withName("Task 1").build();
    private Task task2 = Task.newBuilder().withName("Task 2").build();
    private TaskList listWithTasks = TaskList.newBuilder(taskList).withTasks(asList(task1, task2)).build();
    private Task taskToAdd = Task.newBuilder().withName("Task 3").build();

    @Before
    public void setUp() {
        repository = mock(TaskListRepository.class);
        service = new TaskListService(repository);

        when(repository.save(any())).thenReturn(taskList);
        when(repository.findByOwnerId(any())).thenReturn(Arrays.asList(taskList, anotherTaskList));
        when(repository.findById(any())).thenReturn(of(listWithTasks));
    }

    @Test
    public void createPersonalList_callsRepository() {
        service.createPersonalList(userId, listName);
        verify(repository).save(TaskList.newBuilder().withName(listName).withOwnerId(userId).build());
    }

    @Test
    public void createPersonalList_returnsCreatedList() {
        TaskList createdList = service.createPersonalList(userId, listName);
        assertThat(createdList).isEqualTo(taskList);
    }

    @Test
    public void getAllPersonalLists_callsRepository() {
        service.getAllPersonalLists(userId);
        verify(repository).findByOwnerId(userId);
    }

    @Test
    public void getAllPersonalLists_returnsAList() {
        List<TaskList> lists = service.getAllPersonalLists(userId);
        assertThat(lists).containsExactlyInAnyOrder(taskList, anotherTaskList);
    }

    @Test
    public void getTasksInList_callsRepository() {
        service.getTasksInList(listWithTasks.getId());
        verify(repository).findById(listWithTasks.getId());
    }

    @Test
    public void getTasksInList_returnsAList() {
        List<Task> tasks = service.getTasksInList(listWithTasks.getId());
        assertThat(tasks).isEqualTo(listWithTasks.getTasks());
    }

    @Test
    public void addTaskToList_updatesTaskListWithNewTask() {
        service.addTaskToList(listWithTasks.getId(), taskToAdd);
        TaskList updatedTaskList = TaskList.newBuilder(listWithTasks).withTasks(asList(task1, task2, taskToAdd)).build();
        verify(repository).save(updatedTaskList);
    }

    @Test
    public void addTaskToList_returnsListOfTasks() {
        when(repository.save(any())).thenReturn(TaskList.newBuilder(taskList).withTasks(asList(task1, task2, taskToAdd)).build());
        List<Task> tasks = service.addTaskToList(listWithTasks.getId(), taskToAdd);
        assertThat(tasks).containsExactly(task1, task2, taskToAdd);
    }

    @Test
    public void addTaskToList_canAddTaskToNullList() {
        when(repository.findById(taskList.getId())).thenReturn(of(taskList));
        service.addTaskToList(taskList.getId(), taskToAdd);
        verify(repository).save(TaskList.newBuilder()
                .withId(listId)
                .withName(listName)
                .withOwnerId(userId)
                .withTasks(singletonList(taskToAdd))
                .build()
        );
    }
}