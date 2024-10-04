package helloworld.studytogether.controller;


import helloworld.studytogether.dto.JoinDTO;
import helloworld.studytogether.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService){

        this.joinService = joinService;

    }


    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinDTO joinDTO) {
        System.out.println("data =" + joinDTO);
        joinService.joinProcess(joinDTO);
        return "join";
    }
}