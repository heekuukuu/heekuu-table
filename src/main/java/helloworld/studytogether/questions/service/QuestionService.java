package helloworld.studytogether.questions.service;

import helloworld.studytogether.questions.dto.GetQuestionResponseDto;
import helloworld.studytogether.questions.dto.QuestionRequest;
import helloworld.studytogether.questions.entity.Question;
import helloworld.studytogether.questions.type.SubjectNames;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

  // 질문저장
  Question saveQuestion(QuestionRequest requestDto, Long userId) throws IOException;

  // 개인 질문조회
  Page<GetQuestionResponseDto> getQuestionList(Long userId, Pageable pageable);

  // 개인 과목별 조회
  //Page<GetQuestionResponseDto> getQuestionListBySubject(Long userId, SubjectNames subjectNames,Pageable pageable);


  // 모든 사용자용
  Page<GetQuestionResponseDto> getAllQuestions(Pageable pageable);

  // 모든 사용자 과목별 조회
  Page<GetQuestionResponseDto> getAllQuestionsBySubject(SubjectNames subject, Pageable pageable);
}
