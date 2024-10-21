package helloworld.studytogether.questions.service;

import helloworld.studytogether.questions.dto.AddQuestionRequestDto;
import helloworld.studytogether.questions.entity.Question;
import java.io.IOException;

public interface QuestionService {
  Question saveQuestion(AddQuestionRequestDto requestDto) throws IOException;
}
