package jpa.basic.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
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
		
	}
}
