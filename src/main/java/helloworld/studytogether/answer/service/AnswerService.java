package helloworld.studytogether.answer.service;

import helloworld.studytogether.answer.dto.AnswerDTO;

public interface AnswerService {
    AnswerDTO createAnswer(AnswerDTO answerDTO);
    AnswerDTO getAnswerById(Long id);
    AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO);
    void deleteAnswer(Long id);
}