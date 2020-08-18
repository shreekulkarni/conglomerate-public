package com.conglomerate.dev.services;

import com.conglomerate.dev.Exceptions.*;
import com.conglomerate.dev.models.*;
import com.conglomerate.dev.models.domain.AddUserDomain;
import com.conglomerate.dev.models.domain.GoogleTokenDomain;
import com.conglomerate.dev.models.domain.LoginRequestDomain;
import com.conglomerate.dev.models.domain.NewPasswordDomain;
import com.conglomerate.dev.repositories.*;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final GroupingRepository groupingRepository;
    private final ResetPinRepository resetPinRepository;
    private final MessageRepository messageRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final DeviceRepository deviceRepository;
    private final JavaMailSender javaMailSender;

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe


    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public UserService(UserRepository userRepository,
                       GroupingRepository groupingRepository,
                       ResetPinRepository resetPinRepository,
                       MessageRepository messageRepository,
                       DocumentRepository documentRepository,
                       DocumentService documentService,
                       DeviceRepository deviceRepository,
                       JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.groupingRepository = groupingRepository;
        this.resetPinRepository = resetPinRepository;
        this.messageRepository = messageRepository;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.deviceRepository = deviceRepository;
        this.javaMailSender = javaMailSender;
    }


    public List<User> getAllUsers() {
        // this is the logic for the controller endpoint -- it's a simple service so there isn't much logic
        return userRepository.findAll();
    }


    // Adds the user to the database
    public AddUserDomain addUser(AddUserDomain userDomain) {

        if (check_username_exists(userDomain.getUserName())) {
            System.out.println("username found");
            throw new DuplicateUsernameException();
        }

        if (check_email_exists(userDomain.getEmail())) {
            System.out.println("email found");
            throw new DuplicateEmailException();
        }

        String passwordSha256hash = Hashing.sha256()
                .hashString(userDomain.getPasswordHash(), StandardCharsets.UTF_8)
                .toString();

        User user = User.builder()
                .userName(userDomain.getUserName())
                .email(userDomain.getEmail())
                .passwordHash(passwordSha256hash)
                .calendarLink(null)
                .profilePic(null)
                .build();

        userRepository.save(user);

        return AddUserDomain.builder()
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .userName(user.getUserName())
                .build();
    }


    public void deleteAccount(String authToken) {
        String authTokenHash = hash(authToken);

        Optional<User> maybeToDelete = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeToDelete.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User toDelete = maybeToDelete.get();

        // remove the user from its groups
        List<Grouping> ownedGroups = groupingRepository.findByOwner(toDelete);
        for (Grouping grouping : ownedGroups) {
            grouping.setOwner(null);
            groupingRepository.save(grouping);
        }

        List<Grouping> groupings = groupingRepository.findByMembersContains(toDelete);
        for (Grouping grouping : groupings) {
            grouping.getMembers().remove(toDelete);
            groupingRepository.save(grouping);
        }
        
        // remove the user from the "sender" field of messages
        List<Message> messages = messageRepository.findBySender(toDelete);
        for (Message message : messages) {
            message.setSender(null);
            messageRepository.save(message);
        }

        List<Message> readMessages = messageRepository.findByReadContains(toDelete);
        for (Message message : readMessages) {
            message.getRead().remove(toDelete);
            messageRepository.save(message);
        }

        // remove the user from uploader field of couments
        List<Document> documents = documentRepository.findByUploader(toDelete);
        for (Document doc : documents) {
            if (!doc.isShared()) {
                documentService.delete(authToken, doc.getId());
            } else {
                doc.setUploader(null);
                documentRepository.save(doc);
            }
        }

        // TODO: make sure nothing the user uploaded/created gets deleted
        userRepository.delete(toDelete);
    }


    public String login(LoginRequestDomain loginRequest) {

        Optional<User> maybeUser = userRepository.getByUserName(loginRequest.getUsername());
        if (!maybeUser.isPresent()) {
            throw new NoSuchUserException(loginRequest.getUsername());
        }
        User user = maybeUser.get();

        String hashedPassword = hash(loginRequest.getPassword());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            throw new InvalidLoginException();
        } else {
            String authToken = generateNewToken();
            String authTokenHash = hash(authToken);
            user.setAuthTokenHash(authTokenHash);
            userRepository.save(user);

            System.out.println("Generated " + authToken);

            return authToken;
        }
    }


    public List<Device> getDevices(String authToken) {
        String authtokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authtokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        return user.getDevices();
    }


    public int registerDevice(String authToken, String deviceToken) {
        String authtokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authtokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        // don't give an error on registering the same device token, but don't add to database
        for (Device existingDevice : user.getDevices())
            if (existingDevice.getDeviceToken().equals(deviceToken))
                return -1;

        Device newDevice = Device.builder()
                .deviceToken(deviceToken)
                .user(user)
                .build();

        deviceRepository.save(newDevice);
        return newDevice.getId();
    }


    public void setProfilePicture(String authToken, String newProfilePicture) throws InvalidAuthTokenException{
        String authtokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authtokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        User user = maybeUser.get();
        user.setProfilePic(newProfilePicture);
        userRepository.save(user);
    }


    public String getProfilePicture(String authToken) {
        String authtokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authtokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        return maybeUser.get().getProfilePic();
    }


    public boolean check_username_exists(String username) {
        List<User> users = userRepository.findAllByUserName(username);
        return !users.isEmpty();
    }


    public boolean check_email_exists(String email) {
        List<User> users = userRepository.findAllByEmail(email);
        return !users.isEmpty();
    }


    public void forgotPassword(String username, String email) {
        // make sure user exists
        if (!check_username_exists(username)){
            throw new NoSuchUserException(username);
        }

        // can skip checking the optional because we know it's present
        User user = userRepository.findAllByUserName(username).get(0);

        if (!email.equals(user.getEmail())) {
            throw new WrongEmailException(username, email);
        }

        int pin = (int) (Math.random() * 10000);

        String hashedPin = hash(pin + "");

        ResetPin resetPin = ResetPin.builder()
                .hashedPin(hashedPin)
                .user(user)
                .expiration(LocalDateTime.now().plusMinutes(15))
                .build();

        resetPinRepository.save(resetPin);

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("Reset Your Conglomerate Password");
        simpleMailMessage.setText("Here is your pin:\n" + pin + "\n");
        simpleMailMessage.setFrom("conglomerateapp@gmail.com");
        javaMailSender.send(simpleMailMessage);
        System.out.println("Mail sent!");
    }


    public void updatePassword(String username, NewPasswordDomain newPasswordDomain) {
        // make sure the user exists
        if (!check_username_exists(username)) {
            throw new NoSuchUserException(username);
        }

        // get the user with that username
        User user = userRepository.findAllByUserName(username).get(0);

        // hash the pin from client to compare with hashed pin in database
        String hashedPin = hash(newPasswordDomain.getResetPin());

        // try to get the hashed pin in database that corresponds with the user
        Optional<ResetPin> maybeSentResetPin = resetPinRepository.findByHashedPinAndUser(hashedPin, user);
        if (!maybeSentResetPin.isPresent()) {
            // if there is no pin, throw an exception
            throw new NoResetPinException(user.getUserName());
        }
        ResetPin sentResetPin = maybeSentResetPin.get();


        // finally, update the user's password with a hashed version of the given password
        String hashedPassword = hash(newPasswordDomain.getNewPassword());
        user.setPasswordHash(hashedPassword);

        // and save it back into the database
        userRepository.save(user);
    }


    public int linkGoogleAccount(String authToken, GoogleTokenDomain tokens) {
        String authTokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        user.setGoogleIdToken(tokens.getIdToken());
        user.setGoogleRefreshToken(tokens.getRefreshToken());

        userRepository.save(user);
        return user.getId();
    }

    public int unlinkGoogleAccount(String authToken) {
        String authTokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        user.setGoogleIdToken(null);
        user.setGoogleRefreshToken(null);

        userRepository.save(user);
        return user.getId();
    }

    public boolean hasLinkedGoogleAccount(String authToken) {
        String authTokenHash = hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        System.out.println("ID TOKEN: " + user.getGoogleIdToken());
        System.out.println("REFRESH TOKEN: " + user.getGoogleRefreshToken());
        return !(user.getGoogleIdToken() == null && user.getGoogleRefreshToken() == null);
    }


    // runs every 5 minutes
    @Scheduled(fixedRate=300000)
    public void cleanUpResetPins() {
        // finds all pins with an expiration that has passed
        List<ResetPin> toBeRemoved = resetPinRepository.findByExpirationBefore(LocalDateTime.now());

        // removes them from the list
        for (ResetPin resetPin : toBeRemoved) {
            resetPinRepository.delete(resetPin);
        }

        // prints out results
        System.out.println("Cleaned ResetPin table at " + LocalDateTime.now() + " - "
                + toBeRemoved.size() + " pins removed");
    }


    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static String hash(String toHash) {
        return Hashing.sha256()
                .hashString(toHash, StandardCharsets.UTF_8)
                .toString();
    }
}
