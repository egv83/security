package com.ochobits.security.service;


import com.ochobits.security.controller.dto.AuthCreateRoleRequest;
import com.ochobits.security.controller.dto.AuthCreateUserRequest;
import com.ochobits.security.controller.dto.AuthLoginRequest;
import com.ochobits.security.controller.dto.AuthResponse;
import com.ochobits.security.persistance.entity.RoleEntity;
import com.ochobits.security.persistance.entity.UserEntity;
import com.ochobits.security.persistance.repository.RoleRepository;
import com.ochobits.security.persistance.repository.UserRepository;
import com.ochobits.security.util.JwtUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private JwtUtils jwtUtils;

    public UserDetailServiceImpl(PasswordEncoder passwordEncoder, JwtUtils jwtUtils, UserRepository userRepository,
                                 RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

//                UserEntity userEntity = userRepository.findUserEntityByUsername(username)
//                        .orElseThrow(()-> new UsernameNotFoundException("El usuario: " + username + " no existe"));
        UserEntity userEntity = userRepository.findUserEntityByUsername(username).orElse(null);

//        if(Objects.isNull(userEntity)) { /*ESTO APLICO SI QUIERO QUE ESTE MENSAJE DE ERROR LLEGUE A LA PARTE MAS ALTA PARA DAR A CONOCER AL CLIENTE*/
//            throw new UsernameNotFoundException("El usuario: " + username + " no existe");
//        }

        if(Objects.nonNull(userEntity)) {

            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

            userEntity.getRoles()
                    .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

            userEntity.getRoles().stream()
                    .flatMap(role -> role.getPermissionList().stream())
                    .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

            return new User(userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.isEnable(),
                    userEntity.isAccountNoExpired(),
                    userEntity.isCredentialNoExpired(),
                    userEntity.isAccountNoLocked(),
                    authorityList);
        }
        return null;
    }

    public AuthResponse loginUser(AuthLoginRequest request) {
        String userName = request.userName();
        String password = request.password();

        try{
            Authentication  authentication = this.authenticate(userName,password);

            System.out.println("LOGINUSER AUTHENTICATION: "+authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtUtils.createToken(authentication);

            AuthResponse authResponse = new AuthResponse(userName,"User loged successfuly",accessToken,true);
            return authResponse;
        }catch (UsernameNotFoundException e){
            System.out.println("Error: "+e.getMessage());
            return new AuthResponse(userName,e.getMessage(),"",false);
        }
//        return new AuthResponse(userName,"User not exist",accessToken,true);
    }

    public Authentication authenticate(String userName, String password) throws UsernameNotFoundException{
        UserDetails userDetails = this.loadUserByUsername(userName);

        if(Objects.isNull(userDetails)){
            System.out.println("Invalid username or password");
            throw new UsernameNotFoundException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password,userDetails.getPassword())){
            System.out.println("Invalid password");
            throw new UsernameNotFoundException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userName,userDetails.getPassword(),userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest request){
        String userName = request.userName();
        String password = request.password();
        List<String> roleRequest = request.roleRequest().roleListName();

        Set<RoleEntity> roleEntitySet = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest)
                .stream()
                .collect(Collectors.toSet());

        if(Objects.isNull(roleEntitySet)){
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(userName)
                .password(passwordEncoder.encode(password))
                .roles(roleEntitySet)
                .isEnable(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
        UserEntity userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();/*SETAER LOS PERMISOS QUE VA A TENER EL USUARIO*/

        userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_"
                .concat(role.getRoleEnum().name())))); /*EN ESTA LISTA OBTENGO LOS ROLES DEL UAURIO Y ASIGNO AL SIMPLE GRANTED AUTHORITI ANTEPONIENDO LA PARABLA ROLE_ PARA QUE SE AGREQUE COMO LO IDENTIFICA SPRING*/

        /*ASIGNAR LOS PRMISOS CORRESPONDIENTES AL ROL*/
        userCreated.getRoles().stream()/*EL GETROLE LO COMBIERTO A UN STREAM PARA PODER TRABAJAR CON EL*/
                .flatMap(role -> role.getPermissionList().stream())/*ASIGNO COMO FLATMAP PARA PODER OBTENER LOS PERMISOS DEL ROLE Y LO CONVIERTO A UN STREAM*/
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName()))); /*EN EL PERMISSION AGREGO LOS PERMISOS CORESPONDIENTES AL ROL*/
        /*SE CREA EL OBJETO DE AUTENTICACIÓN*/
        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(),
                userCreated.getPassword(),authorityList);

        /*SE CREA EL TOKEN*/
        String accesToken = jwtUtils.createToken(authentication);

        AuthResponse response = new AuthResponse(userCreated.getUsername(),"User Created successfully",accesToken,true);
        return response;

    }
}
