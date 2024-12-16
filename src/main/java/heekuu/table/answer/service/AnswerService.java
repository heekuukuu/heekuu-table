package heekuu.table.answer.service;


import heekuu.table.answer.dto.AnswerDTO;

public interface AnswerService {
    AnswerDTO createAnswer(AnswerDTO answerDTO);
    AnswerDTO getAnswerById(Long id);
    AnswerDTO updateAnswer(Long id, AnswerDTO answerDTO);
    void deleteAnswer(Long id);
    void likeAnswer(Long answerId);
    void unlikeAnswer(Long answerId);
    void selectAnswer(Long questionId, Long answerId);
}
