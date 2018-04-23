package com.dengzhanglin.favoriteapi.controller;

import com.alibaba.fastjson.JSONObject;
import com.dengzhanglin.favoriteapi.domain.RegisterVerifyCode;
import com.dengzhanglin.favoriteapi.domain.User;
import com.dengzhanglin.favoriteapi.payload.*;
import com.dengzhanglin.favoriteapi.repository.RegisterVerifyCodeRepository;
import com.dengzhanglin.favoriteapi.repository.UserRepository;
import com.dengzhanglin.favoriteapi.security.JwtTokenProvider;
import com.dengzhanglin.favoriteapi.service.SesService;
import com.dengzhanglin.favoriteapi.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider tokenProvider;
    @Autowired
    SesService sesService;
    @Autowired
    RegisterVerifyCodeRepository registerVerifyCodeRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JSONObject() {{
            put("code", 0);
            put("message", "OK");
            put("token", token);
        }});
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        String email = registerRequest.getEmail();
        String loggerInfo = "[register]" + email;

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            // return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
            logger.info(loggerInfo + " 邮箱已经注册");
            return ResponseEntity.ok(new ApiResponse(false, "该邮箱已经注册"));
        }

        // 检查验证码
        Optional<RegisterVerifyCode> last = registerVerifyCodeRepository.findLastByEmail(email);
        if (!last.isPresent()) {
            logger.info(loggerInfo + " 没有找到最后一个验证码");
            return ResponseEntity.ok(new ApiResponse(false, "找不到该邮箱的验证码"));
        }

        String verifyCode = registerRequest.getVerifyCode();
        RegisterVerifyCode lastCode = last.get();

        // 验证码是否正确
        if (!lastCode.getCode().equals(verifyCode)) {
            // TODO 纪录注册验证码错误次数，防止被暴力破解
            logger.info(loggerInfo + " 验证码错误");
            return ResponseEntity.ok(new ApiResponse(false, "验证码错误"));
        }

        // 当前时间
        Instant instant = Instant.now();
        // 是否过期
        if (instant.isAfter(lastCode.getExpiredAt())) {
            logger.info(loggerInfo + " 验证码已经过期");
            return ResponseEntity.ok(new ApiResponse(false, "验证码已经过期"));
        }

        User user = new User();
        user.setCreatedAt(instant);
        user.setUpdatedAt(instant);
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User result = userRepository.save(user);

        logger.info(loggerInfo + " " + result.toString());

        return ResponseEntity.ok(new JSONObject() {{
            put("code", 0);
            put("message", "OK");
            put("token", tokenProvider.generateToken(user));
        }});
    }

    @PostMapping(value = "/verifyCode")
    public Object verifyCode(@Valid @RequestBody VerifyCodeRequest request) {

        logger.info("[verifyCode]" + request.getEmail());

        final String email = request.getEmail();

        // check email
        if (userRepository.existsByEmail(email)) {
            logger.info("[verifyCode]" + email + " 邮箱已经注册");
            return ResponseEntity.ok(new ApiResponse(false, "该邮箱已经注册"));
        }

        // now
        Instant instant = Instant.now();


        // 上次发送时间
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "id");
        Page<RegisterVerifyCode> pages = registerVerifyCodeRepository.findByEmail(email, pageable);
        if (pages.getTotalElements() > 0) {
            pages.iterator();
            while (pages.iterator().hasNext()) {
                RegisterVerifyCode temp = pages.iterator().next();
                // 2 分钟前
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, -2);
                // 2 分钟之前的时间还在 created_at 之前，说明时间还没有超过 2 分钟
                logger.info("[verifyCode]" + temp.getCreatedAt().toString() + " " + cal.toString());
                if (cal.toInstant().isBefore(temp.getCreatedAt())) {
                    logger.info("[verifyCode]" + email + " 操作频繁，请稍后再试");
                    return ResponseEntity.ok(new ApiResponse(false, "操作频繁，请稍后再试"));
                }
                break;
            }
        }

        // 当天发送次数不能超过 20 次
        // yyyy-MM-dd HH:mm:s
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(format.format(new Date()) + " 00:00:00");
            if (registerVerifyCodeRepository.countByEmailAndCreatedAtAfter(email, date.toInstant()) > 19) {
                logger.info("[verifyCode]" + email + " 当天累计发送邮件已经达到 20 ");
                return ResponseEntity.ok(new ApiResponse(false, "当天累计发送邮件已经达到 20 封"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger.info("[verifyCode]" + email + " " + e.getMessage());
            return ResponseEntity.ok(new ApiResponse(false, "服务器错误"));
        }

        // code
        String code = RandomUtil.number(6);

        if (this.sesService.send(request.getEmail(), "注册验证码",
                "您的验证码是：<strong>" + code + "</strong>，15分钟内有效，请及时输入。")) {
            RegisterVerifyCode verifyCode = new RegisterVerifyCode();

            verifyCode.setEmail(email);
            verifyCode.setCode(code);


            verifyCode.setCreatedAt(instant);

            // add 15 minutes
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 15);
            verifyCode.setExpiredAt(calendar.toInstant());

            // TODO 检查是否保存成功
            registerVerifyCodeRepository.save(verifyCode);
        }

        return ResponseEntity.ok(new ApiResponse(true, "OK"));
    }
}
