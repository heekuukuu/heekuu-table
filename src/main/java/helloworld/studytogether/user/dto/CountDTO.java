package helloworld.studytogether.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CountDTO {
    private int questionCount;
    private int answerCount;
    private int selectedAnswerCount;
}
