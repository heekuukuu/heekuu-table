package helloworld.studytogether.questions.service;

import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.QuestionRequest;
import helloworld.studytogether.questions.dto.UpdateQuestionRequest;
import helloworld.studytogether.questions.dto.UpdateQuestionResponse;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.type.SubjectNames;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

  Question saveQuestion(QuestionRequest requestDto, Long userId) throws IOException;
  UpdateQuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request);
  Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable);
  Page<GetQuestionResponseDto> getQuestionListBySubject(Long userId, SubjectNames subjectNames, Pageable pageable);
  Page<GetQuestionResponseDto> getQuestionsBySolvedStatus(Long userId, Boolean isSolved, Pageable pageable);
  Page<GetQuestionResponseDto> getAllQuestions(Pageable pageable);
  Page<GetQuestionResponseDto> getAllQuestionsBySubject(SubjectNames subject, Pageable pageable);
  Page<GetQuestionResponseDto> getUserQuestionsBySubject(Long userId, SubjectNames subject, Pageable pageable);
}
