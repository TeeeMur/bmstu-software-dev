package com.example.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.backend.model.Museum;
import com.example.backend.model.User;
import com.example.backend.repositories.MuseumRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.tools.DataValidationException;
import com.example.backend.tools.Utils;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable(value = "id") Long userId)
            throws DataValidationException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Картина с таким индексом не найдена"));
        return ResponseEntity.ok(user);
    }
    
    @GetMapping()
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return ResponseEntity.ok(userRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "login"))));
    }

    @PostMapping()
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            User nu = userRepository.save(user);
            return new ResponseEntity<Object>(nu, HttpStatus.OK);
        } catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("user_email_key") || ex.getMessage().contains("user_login_key")) {
                error = "useralreadyexists";
            } else {
                error = "undefinederror";
            }
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
                                                    @Validated @RequestBody User userDetails)
            throws DataValidationException
    {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException(" Пользователь с таким индексом не найден"));
            user.email = userDetails.email;
            String np = userDetails.np;
            if (np != null  && !np.isEmpty()) {
                    byte[] b = new byte[32];
                    new Random().nextBytes(b);
                    String salt = new String(Hex.encode(b));
                    user.password = Utils.ComputeHash(np, salt);
                    user.salt = salt;
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception ex) {
            if (ex.getMessage().contains("email"))
                throw new DataValidationException("Пользователь с такой почтой уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
            @Validated @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                Optional<Museum> mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId,
                                @Validated @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                u.removeMuseum(m);
                cnt++;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();
        if (user.isPresent()) {
            userRepository.delete(user.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/deleteusers")
    public ResponseEntity<Object> deleteUsers(@RequestBody List<User> users) {
        userRepository.deleteAll(users);
        return new ResponseEntity(HttpStatus.OK);
    }

}
