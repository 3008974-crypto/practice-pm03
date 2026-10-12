package practice.demo;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class PracticeDemo {
	public static void main(String[] args) {
		System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

		int studentAge = 20;

		System.out.println("Учебная практика по ПМ.03");
		System.out.println("Группа: ОКИП-12309ТБИ");
		System.out.println("Возраст: " + studentAge + " лет");
	}
}