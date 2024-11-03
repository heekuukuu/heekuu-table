package heekuu.news.questions.service;

import heekuu.news.questions.dto.GetQuestionResponseDto;
import heekuu.news.questions.dto.QuestionRequest;
import heekuu.news.questions.dto.UpdateQuestionRequest;
import heekuu.news.questions.dto.UpdateQuestionResponse;
import heekuu.news.questions.entity.Question;
import heekuu.news.questions.type.SubjectNames;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

  Question saveQuestion(QuestionRequest requestDto, Long userId) throws IOException;
  UpdateQuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request);
  void deleteQuestion(Long questionId);
  Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable);
  Page<GetQuestionResponseDto> getQuestionListBySubject(Long userId, SubjectNames subjectNames, Pageable pageable);
  Page<GetQuestionResponseDto> getQuestionsBySolvedStatus(Long userId, Boolean isSolved, Pageable pageable);
  Page<GetQuestionResponseDto> getAllQuestions(Pageable pageable);
  Page<GetQuestionResponseDto> getAllQuestionsBySubject(SubjectNames subject, Pageable pageable);
  Page<GetQuestionResponseDto> getUserQuestionsBySubject(Long userId, SubjectNames subject, Pageable pageable);
}
