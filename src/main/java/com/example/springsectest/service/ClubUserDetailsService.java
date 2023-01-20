package com.example.springsectest.service;

import com.example.springsectest.dto.ClubAuthMemberDto;
import com.example.springsectest.dto.ClubMemberDto;
import com.example.springsectest.dto.ClubMemberRole;
import com.example.springsectest.mapper.ClubMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

// 인증정보를 거치면 UserDetailsService의 loadUserByUsername가 실행된다.
// 사용자 정보를 데이터 베이스에서 가져올경우 UserDetailsService를 상속받아
// loadUserByUsername를 구현해야한다.
@Service
@RequiredArgsConstructor
public class ClubUserDetailsService implements UserDetailsService {
    // Mybatis의 mapper를 이용하여 사용자 정보를 가져온다.
    private final ClubMemberMapper clubMemberMapper;
    
    // DB에서 사용자 정보를 가져올 경우 반드시 UserDetailsService의 loadUserByUsername() 메서드를 구현해야한다.
    // 따로 호출을 하지않아도 구현만하면
    // loadUserByUsername() 메서드는 UserDetails 인터페이스를 구현한 클래스 객체를 반환하면 스프링 시큐리티에서 확인하여
    // 인증된 사용자인지 아닌지를 확인한다.
    // 사용자가 로그인 페이지에서 로그인 시 스프링 시큐리티가 먼저 데이터를 받아서 loadUserByUsername() 메서드에
    // 사용자 ID를 매개변수로 사용해서 실행
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Optional : DB 사용 시 데이터 조회 후 데이터가 null이 들어올 경우 오류가 발생할 수 있는 부분을
        // 안전하게 사용하기 위한 제네릭타입
        Optional<ClubMemberDto> result = clubMemberMapper.findByEmail(username, false);
        
        // DB에 해당 사용자 정보가 있는지 확인
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("이메일 및 비밀번호를 확인하세요.");
        }
        
        // Optional 타입에 저장된 정보를 가져옴
        ClubMemberDto member = result.get();
        // 데이터 베이스에 등급 권한 정보 컬럼이 있으면 필요없음
        // 사용자 정보에 사용 등급 권한을 설정
        member.addMemberRole(ClubMemberRole.USER);
        
        // 로그인 인증 정보를 가지고 있는 ClubAuthMemberDto 클래스 타입의 객체를 생성한다.
        // 매개변수로 DB에서 가져온 정보를 넘겨서 사용자가 입력한 사용자 ID를 가지고 있는 로그인 인증된 객체가
        // 생성이 된다.
        ClubAuthMemberDto clubAuthMember = new ClubAuthMemberDto(
                member.getEmail(),
                member.getPassword(),
                member.isFormSocial(),
                // 스프링 시큐리티에서 사용하는 권한 정보는 모두 ROLE_권한 형태로 부여
                member.getRoleSet().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toSet())
        );
        
        clubAuthMember.setName(member.getName());
        clubAuthMember.setFromSocial(member.isFormSocial());
        
        // 로그인 인증 정보를 가지고 있는 객체를 반환 시 스프링 시큐리티가 처리해준다.
        return clubAuthMember;
    }
}
