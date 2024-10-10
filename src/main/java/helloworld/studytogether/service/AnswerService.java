package helloworld.studytogether.service;

import helloworld.studytogether.dto.AnswerDTO;

public interface AnswerService {
    AnswerDTO createAnswer(AnswerDTO answerDTO);
    AnswerDTO getAnswerById(Long id);
    AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO);
    void deleteAnswer(Long id);
}