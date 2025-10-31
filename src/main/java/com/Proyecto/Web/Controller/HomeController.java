    package com.Proyecto.Web.Controller;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;

    import com.Proyecto.Web.Service.UsuarioService;

    @Controller
    public class HomeController {

        @Autowired
        private UsuarioService usuarioService;

        @GetMapping("/login")
        public String loginForm() {
            return "Login"; // nombre de tu template Login.html
        }


        @GetMapping("/home")
        public String home() {
            return "home"; // template Home.html
        }

    
        

    }
    
