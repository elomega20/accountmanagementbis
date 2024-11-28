package groupeisi.com.accountManagement.servlet;

import groupeisi.com.accountManagement.entity.User;
import groupeisi.com.accountManagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Gestion Account Controller", description = "Permet de gérer les comptes")
public class UserController {
    private final UserService userService;

    @Operation(summary = "pour obtenir tout les utilisateurs")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "pour obtenir un utilisateur via son Id")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "pour cree un utilisateur")
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @Operation(summary = "pour mettre a jour utilisateur")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @Operation(summary = "pour supprimer un utilisateur via son Id")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
// test test