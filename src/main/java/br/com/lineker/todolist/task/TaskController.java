package br.com.lineker.todolist.task;

import br.com.lineker.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    public TaskRepository taskRepository;
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
      var idUser = request.getAttribute("idUser");
       taskModel.setIdUser((UUID) idUser);

       var currentDate = LocalDateTime.now();
       if (currentDate.isAfter(taskModel.getStarAt()) || currentDate.isAfter(taskModel.getEndAt())){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio de ser maior que a data atual");
       }
        if (taskModel.getStarAt().isAfter(taskModel.getEndAt()) ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio de ser maior que a data atual");
        }
       var task =  this.taskRepository.save(taskModel);
       return  ResponseEntity.status(HttpStatus.OK).body(task);

    }

    @GetMapping("/")
    public List<TaskModel> List(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
      var tasks =  this.taskRepository.findByIdUser((UUID) idUser);
      return tasks;
    }
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){

       var task = this.taskRepository.findById(id).orElse(null);

       if (task == null){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                   body("Tarefa Não encontrada");

       }

        var idUser = request.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body("Usuario nao tem premissao para alterar essa tarefa");
        }

           Utils.copyNonNullProperties(taskModel, task);
            var taskUpadated = this.taskRepository.save(task);
             return ResponseEntity.ok().body(taskUpadated);

    }

}
