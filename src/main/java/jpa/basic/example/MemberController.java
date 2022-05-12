package jpa.basic.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberRepository mr;
	
	@GetMapping("/test")
	public void createMember() {
//		mr.createMember();
//		mr.createParentWithChild();
//		mr.findMember();
	}
	
	@GetMapping("/jpqlTest")
	public void jpqlTest() {
		mr.type();
	}
}
