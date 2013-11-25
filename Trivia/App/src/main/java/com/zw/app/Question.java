package com.zw.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weizhang on 11/24/13.
 */
public class Question {
    String question;
    String questionType;
    String imageUrl;
    String correctAnswer;
    List<String> incorrectAnswers=new ArrayList<String>();
    String answer;




    public void setQuestion(String question) {
        this.question = question;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getQuestion() {
        return question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }


    public String getAnswer() {
        return answer;
    }
}
