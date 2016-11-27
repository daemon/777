package net.rocketeer.sevens.database;

@FunctionalInterface
public interface SQLRunnable<T> {
  T run() throws Exception;
}
