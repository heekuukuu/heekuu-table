package heekuu.table.questions.service;

import heekuu.table.questions.dto.GetQuestionResponseDto;
import heekuu.table.questions.dto.QuestionRequest;
import heekuu.table.questions.dto.UpdateQuestionRequest;
import heekuu.table.questions.dto.UpdateQuestionResponse;
import heekuu.table.questions.entity.Question;
import heekuu.table.questions.type.Category;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

  Question saveQuestion(QuestionRequest requestDto, Long userId) throws IOException;
  UpdateQuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest request);
  void deleteQuestion(Long questionId);
  Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable);
  //Page<GetQuestionResponseDto> getAllQuestionsBySubject(Long userId, Category category, Pageable pageable);
  Page<GetQuestionResponseDto> getQuestionsBySolvedStatus(Long userId, Boolean isSolved, Pageable pageable);
  Page<GetQuestionResponseDto> getAllQuestions(Pageable pageable);
  Page<GetQuestionResponseDto> getAllQuestionsByCategory(Category category, Pageable pageable);
  Page<GetQuestionResponseDto> getUserQuestionsByCategory(Long userId, Category subject, Pageable pageable);
}
