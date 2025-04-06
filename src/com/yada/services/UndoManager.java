package com.yada.services;

import java.util.Stack;

public class UndoManager {
    private Stack<Command> commandStack = new Stack<>();

    public void addCommand(Command cmd) {
        commandStack.push(cmd);
    }

    public void undo() {
        if (!commandStack.isEmpty()) {
            Command cmd = commandStack.pop();
            cmd.undo();
            System.out.println("Undo completed.");
        } else {
            System.out.println("No commands to undo.");
        }
    }
}
