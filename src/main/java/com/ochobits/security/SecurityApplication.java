package com.ochobits.security;

import com.ochobits.security.persistance.entity.PermissionEntity;
import com.ochobits.security.persistance.entity.RoleEntity;
import com.ochobits.security.persistance.entity.RoleEnum;
import com.ochobits.security.persistance.entity.UserEntity;
import com.ochobits.security.persistance.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository){
		return args -> {
			/* CREATE PERMISSIONS */
			PermissionEntity createPermission = PermissionEntity.builder()
					.name("CREATE")
					.build();

			PermissionEntity readPermission = PermissionEntity.builder()
					.name("READ")
					.build();

			PermissionEntity updatePermission = PermissionEntity.builder()
					.name("UPDATE")
					.build();

			PermissionEntity deletePermission = PermissionEntity.builder()
					.name("DELETE")
					.build();

			PermissionEntity refactorPermission = PermissionEntity.builder()
					.name("REFACTOR")
					.build();

			/*CREATE ROLES*/
			RoleEntity adminRole = RoleEntity.builder()
					.roleEnum(RoleEnum.ADMIN)
					.permissionList(Set.of(createPermission,readPermission,updatePermission,deletePermission))
					.build();

			RoleEntity userRole = RoleEntity.builder()
					.roleEnum(RoleEnum.USER)
					.permissionList(Set.of(createPermission,readPermission))
					.build();

			RoleEntity invitedRole = RoleEntity.builder()
					.roleEnum(RoleEnum.INVITED)
					.permissionList(Set.of(readPermission))
					.build();

			RoleEntity developerRole = RoleEntity.builder()
					.roleEnum(RoleEnum.DEVELOPER)
					.permissionList(Set.of(createPermission,readPermission,updatePermission,deletePermission,refactorPermission))
					.build();

			/*CREATE USERS*/
			UserEntity userSantiago = UserEntity.builder()
					.username("santiago")
					.password("$2a$10$PH8fVE8Jg6Dr0xZ.Sg.Po.7fZjDAt3GPqJSaDmrk9ab6k4JRrL64K")
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(adminRole))
					.build();

			UserEntity userDaniel = UserEntity.builder()
					.username("daniel")
					.password("$2a$10$PH8fVE8Jg6Dr0xZ.Sg.Po.7fZjDAt3GPqJSaDmrk9ab6k4JRrL64K")
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(userRole))
					.build();

			UserEntity userAndrea = UserEntity.builder()
					.username("andrea")
					.password("$2a$10$PH8fVE8Jg6Dr0xZ.Sg.Po.7fZjDAt3GPqJSaDmrk9ab6k4JRrL64K")
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(invitedRole))
					.build();

			UserEntity userAngi = UserEntity.builder()
					.username("angi")
					.password("$2a$10$PH8fVE8Jg6Dr0xZ.Sg.Po.7fZjDAt3GPqJSaDmrk9ab6k4JRrL64K")
					.isEnable(true)
					.accountNoExpired(true)
					.accountNoLocked(true)
					.credentialNoExpired(true)
					.roles(Set.of(developerRole))
					.build();

			userRepository.saveAll(List.of(userSantiago,userDaniel,userAndrea,userAngi));
		};
	}
}
