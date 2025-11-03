package com.example.finance;

import com.example.finance.cli.CommandProcessor;

public class Main {
    public static void main(String[] args) {
        CommandProcessor processor = new CommandProcessor();
        processor.start();
    }
}
