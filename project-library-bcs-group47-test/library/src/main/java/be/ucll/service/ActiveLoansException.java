package be.ucll.service;

public class ActiveLoansException extends RuntimeException {
    public ActiveLoansException(String message) {
        super(message);
    }
}