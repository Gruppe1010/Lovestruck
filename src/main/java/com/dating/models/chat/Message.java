package com.dating.models.chat;

public class Message
{
    private String message;
    private String author;
    
    // Constructor
    public Message(){}
    public Message(String message, String author)
    {
        this.message = message;
        this.author = author;
    }
    
    // getters + setter
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }
    public String getAuthor()
    {
        return author;
    }
    public void setAuthor(String author)
    {
        this.author = author;
    }
}
