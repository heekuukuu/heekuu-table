package helloworld.studytogether.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountDTO {
    private int questionCount;
    private int answerCount;
    private int selectedAnswerCount;
}
