package org.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.generation.blogpessoal.model.Usuario;
import org.generation.blogpessoal.repository.UsuarioRepository;
import org.generation.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {


	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com","rootroot", ""));
	}
	
	@Test
	@DisplayName("Cadastrar Um Usuário") public void Usuario() {

	HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario> (new Usuario (0L, "Rafael", "fael@email.com", "5469871", "https://i.imgur.com/JR7kUFU.jpg"));
	ResponseEntity<Usuario> corpoResposta = testRestTemplate .exchange("/usuarios/cadastrar", HttpMethod. POST, corpoRequisicao, Usuario.class);

	assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome()); 
	assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());

	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário") 
	public void naoDeveDuplicarUsuario() {

	usuarioService.cadastrarUsuario (new Usuario(0L, 
			"Daniela da Silva", "daniela_silva@email.com.br", "123465278", "https://i.imgur.com/T12NIp9.jpg"));

	HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario> (new Usuario (0L, 
			"Daniela da Silva", "daniela_silva@email.com.br", "123465278", "https://i.imgur.com/T12NIp9.jpg"));

	ResponseEntity<Usuario>corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

	assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
		
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

	Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario (new Usuario (0L, 
			"Jessica Andrews", "jessica_andrews@email.com.br", "jessica123", "https://i.imgur.com/yDRVeK7.jpg"));
	Usuario usuarioUpdate = new Usuario (usuarioCadastrado.get().getId(),
			"Jessica Andrews Ramos", "jessica_ramos@email.com.br", "jessica123", "https://i.imgur.com/yDRVek7.jpg");
	
	HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
	
	ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root@root.com", "rootroot")
			
	.exchange("/usuarios/atualizar", HttpMethod. PUT, corpoRequisicao, Usuario.class);
	assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
	assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Ana Carolina", "carol@email.com", "13465278", "https://i.imgur.com/FETvs20.jpg"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
				"Rafael", "fael@email.com", "54698718", "https://i.imgur.com/JR7kUFU.jpg"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
	}
	
}