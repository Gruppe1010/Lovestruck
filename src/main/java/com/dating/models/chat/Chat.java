package com.dating.models.chat;

import java.util.ArrayList;

public class Chat
{
    private ArrayList<Message> messageList;
    
    // Constructor
    public Chat(){}
    public Chat(ArrayList<Message> messageList)
    {
        this.messageList = messageList;
    }
    
    // getters + setters
    public ArrayList<Message> getMessageList()
    {
        return messageList;
    }
    public void setMessageList(ArrayList<Message> messageList)
    {
        this.messageList = messageList;
    }
}
