package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameBoardStatus;

import javax.persistence.*;
import java.io.Serializable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.hibernate.criterion.Junction;


@Entity
@Table(name = "GAMEBOARD")
public class GameBoard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    // One-to-many relationship with Player
    @OneToMany(mappedBy = "GameBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<User> players;
    @Column(nullable = false)
    private LocalDate creationDate;
    @Column(nullable = false)
    private GameBoardStatus status;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public GameBoardStatus getStatus() {
        return status;
    }
    public void setStatus(GameBoardStatus status) {
        this.status = status;
    }
    public Set<User> getPlayers(){
        return players;
    }
    public void setPlayers(Set<Player> players){
        this.players = players;
    }
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public GameBoard(){
        GameBoardSpace space1 = new GameBoardSpace(1L, "blueG", 0.17012894, 0.36747851);
        GameBoardSpace space2 = new GameBoardSpace(2L, "blueG", 0.641833811, 0.559097421);
        GameBoardSpace space3 = new GameBoardSpace(3L, "blueG", 0.327722063, 0.420845272);
        GameBoardSpace space4 = new GameBoardSpace(4L, "blueG", 0.600286533, 0.398280802);
        GameBoardSpace space5 = new GameBoardSpace(5L, "blueG", 0.900787966, 0.51217765);
        GameBoardSpace space6 = new GameBoardSpace(6L, "blueG", 0.32234957, 0.704512894);
        GameBoardSpace space7 = new GameBoardSpace(7L, "blueG", 0.403295129, 0.244627507);
        GameBoardSpace space8 = new GameBoardSpace(8L, "blueG", 0.333810888, 0.106017192);
        GameBoardSpace space9 = new GameBoardSpace(9L, "yellow", 0.827722063, 0.644340974);
        GameBoardSpace space10 = new GameBoardSpace(10L, "yellow", 0.876074499, 0.585601719);
        GameBoardSpace space11 = new GameBoardSpace(11L, "gambling", 0.46991404, 0.709527221);
        GameBoardSpace space12 = new GameBoardSpace(12L, "item", 0.893624642, 0.436246418);
        GameBoardSpace space13 = new GameBoardSpace(13L, "yellow", 0.846704871, 0.367120344);
        GameBoardSpace space14 = new GameBoardSpace(14L, "red", 0.790472779, 0.313037249);
        GameBoardSpace space15 = new GameBoardSpace(15L, "blue", 0.723853868, 0.268266476);
        GameBoardSpace space16 = new GameBoardSpace(16L, "card", 0.660458453, 0.150429799);
        GameBoardSpace space17 = new GameBoardSpace(17L, "yellow", 0.599212034, 0.111389685);
        GameBoardSpace space18 = new GameBoardSpace(18L, "yellow", 0.413323782, 0.10530086);
        GameBoardSpace space19 = new GameBoardSpace(19L, "blue", 0.53474212, 0.710243553);
        GameBoardSpace space20 = new GameBoardSpace(20L, "item", 0.285100287, 0.156518625);
        GameBoardSpace space21 = new GameBoardSpace(21L, "yellow", 0.21239255, 0.173352436);
        GameBoardSpace space22 = new GameBoardSpace(22L, "yellow", 0.169054441, 0.232808023);
        GameBoardSpace space23 = new GameBoardSpace(23L, "card", 0.170845272, 0.301575931);
        GameBoardSpace space24 = new GameBoardSpace(24L, "yellow", 0.192335244, 0.665114613);
        GameBoardSpace space25 = new GameBoardSpace(25L, "red", 0.170487106, 0.436246418);
        GameBoardSpace space26 = new GameBoardSpace(26L, "item", 0.169770774, 0.505372493);
        GameBoardSpace space27 = new GameBoardSpace(27L, "yellow", 0.242836676, 0.578438395);
        GameBoardSpace space28 = new GameBoardSpace(28L, "yellow", 0.264326648, 0.517550143);
        GameBoardSpace space29 = new GameBoardSpace(29L, "blue", 0.398638968, 0.488538682);
        GameBoardSpace space30 = new GameBoardSpace(30L, "red", 0.459169054, 0.511103152);
        GameBoardSpace space31 = new GameBoardSpace(31L, "yellow", 0.434813754, 0.563753582);
        GameBoardSpace space32 = new GameBoardSpace(32L, "yellow", 0.421919771, 0.652936963);
        GameBoardSpace space33 = new GameBoardSpace(33L, "black", 0.709885387, 0.618911175);
        GameBoardSpace space34 = new GameBoardSpace(34L, "card", 0.253939828, 0.707378223);
        GameBoardSpace space35 = new GameBoardSpace(35L, "yellow", 0.573424069, 0.559097421);
        GameBoardSpace space36 = new GameBoardSpace(36L, "card", 0.507879656, 0.56482808);
        GameBoardSpace space37 = new GameBoardSpace(37L, "card", 0.744985673, 0.48495702);
        GameBoardSpace space38 = new GameBoardSpace(38L, "item", 0.727793696, 0.417621777);
        GameBoardSpace space39 = new GameBoardSpace(39L, "yellow", 0.726361032, 0.339899713);
        GameBoardSpace space40 = new GameBoardSpace(40L, "card", 0.613538682, 0.238538682);
        GameBoardSpace space41 = new GameBoardSpace(41L, "yellow", 0.549426934, 0.267908309);
        GameBoardSpace space42 = new GameBoardSpace(42L, "yellow", 0.492836676, 0.306948424);
        GameBoardSpace space43 = new GameBoardSpace(43L, "black", 0.456661891, 0.361389685);
        GameBoardSpace space44 = new GameBoardSpace(44L, "item", 0.543696275, 0.423710602);
        GameBoardSpace space45 = new GameBoardSpace(45L, "item", 0.389326648, 0.707020057);
        GameBoardSpace space46 = new GameBoardSpace(46L, "gambling", 0.660458453, 0.35995702);
        GameBoardSpace space47 = new GameBoardSpace(47L, "catnami", 0.764326648, 0.689469914);
        GameBoardSpace space48 = new GameBoardSpace(48L, "catnami", 0.338825215, 0.348853868);
        GameBoardSpace space49 = new GameBoardSpace(49L, "yellow", 0.352793696, 0.281160458);
        GameBoardSpace space50 = new GameBoardSpace(50L, "black", 0.314469914, 0.216690544);
        GameBoardSpace space51 = new GameBoardSpace(51L, "gambling", 0.484598854, 0.156160458);
        GameBoardSpace space52 = new GameBoardSpace(52L, "yellow", 0.612464183, 0.701289398);
        GameBoardSpace start53 = new GameBoardSpace(53L, "startLeft", 0.074498567, 0.42765043);
        GameBoardSpace start54 = new GameBoardSpace(54L, "startRight", 0.819842407, 0.510744986);
        GameBoardSpace specialItem55 = new GameBoardSpace(55L, "specialItem", 0.427292264, 0.607449857);
        GameBoardSpace specialItem56 = new GameBoardSpace(56L, "specialItem", 0.443409742, 0.209885387);
        GameBoardSpace junctionA = new GameBoardSpace(70L, "OnSpace", 0.175143266, 0.591690544);
        GameBoardSpace junctionB = new GameBoardSpace(71L, "OnSpace", 0.686962751, 0.698424069);
        GameBoardSpace junctionC = new GameBoardSpace(72L, "OnSpace", 0.724570201, 0.555873926);
        GameBoardSpace junctionD = new GameBoardSpace(73L, "OnSpace", 0.691618911, 0.20773639);
        GameBoardSpace junctionE = new GameBoardSpace(74L, "OnSpace", 0.514326648, 0.114613181);
        GameBoardSpace gateF = new GameBoardSpace(75L, "OnSpace", 0.325573066, 0.490687679);
        GameBoardSpace gateG = new GameBoardSpace(76L, "OnSpace", 0.453080229, 0.433022923);

        space1.setNext1Space(space25);
        space1.setPrev1Space(space23);
        space2.setNext1Space(space35);
        space2.setPrev1Space(junctionC); //space2.setPrev1Space(space33);
        space3.setNext1Space(space48);
        space3.setPrev1Space(gateF); //space1.setPrev1Space(space28);
        space4.setNext1Space(space46);
        space4.setPrev1Space(space44);
        space5.setNext1Space(space12);
        space5.setPrev1Space(space10);
        space6.setNext1Space(space45);
        space6.setPrev1Space(space34);
        space7.setNext1Space(space49);
        space7.setPrev1Space(specialItem56); //space7.setPrev1Space(space51);
        space8.setNext1Space(space20);
        space8.setPrev1Space(space18);
        space9.setNext1Space(space10);
        space9.setPrev1Space(space47);
        space10.setNext1Space(space5);
        space10.setPrev1Space(space9);
        space11.setNext1Space(space19);
        space11.setPrev1Space(space45);
        space11.setPrev2Space(space32);
        space12.setNext1Space(space13);
        space12.setPrev1Space(space5);
        space13.setNext1Space(space14);
        space13.setPrev1Space(space12);
        space14.setNext1Space(space15);
        space14.setPrev1Space(space13);
        space15.setNext1Space(junctionD); //space15.setNext1Space(space16); space15.setNext2Space(space40);
        space15.setPrev1Space(space14);
        space15.setPrev2Space(space39);
        space16.setNext1Space(space17);
        space16.setPrev1Space(junctionD); //space16.setPrev1Space(space15); space16.setPrev2Space(space40);
        space17.setNext1Space(junctionE); //space17.setNext1Space(space18); space17.setNext2Space(space51);
        space17.setPrev1Space(space16);
        space18.setNext1Space(space8);
        space18.setPrev1Space(junctionE); //space18.setPrev1Space(space17); space18.setPrev1Space(space51);
        space19.setNext1Space(space52);
        space19.setPrev1Space(space11);
        space20.setNext1Space(space21);
        space20.setPrev1Space(space8);
        space20.setPrev2Space(space50);
        space21.setNext1Space(space22);
        space21.setPrev1Space(space20);
        space22.setNext1Space(space23);
        space22.setPrev1Space(space21);
        space23.setNext1Space(space1);
        space23.setPrev1Space(space22);
        space24.setNext1Space(space34);
        space24.setPrev1Space(junctionA); //space24.setPrev1Space(space26);
        space25.setNext1Space(space26);
        space25.setPrev1Space(space1); //space25.setPrev1Space(start53);
        space26.setNext1Space(junctionA); //space26.setNext1Space(space24); space26.setNext1Space(space27);
        space26.setPrev1Space(space25);
        space27.setNext1Space(space28);
        space27.setPrev1Space(junctionA); //space27.setPrev1Space(space26);
        space28.setNext1Space(gateF); //space28.setNext1Space(space29); space28.setNext1Space(space3);
        space28.setPrev1Space(space27);
        space29.setNext1Space(space30);
        space29.setPrev1Space(gateF); //space29.setPrev1Space(space28);
        space30.setNext1Space(space31);
        space30.setPrev1Space(space29);
        space30.setPrev2Space(gateG); //space30.setPrev2Space(space43);
        space31.setNext1Space(specialItem55); // space31.setNext1Space(space32);
        space31.setPrev1Space(space30);
        space32.setNext1Space(space11);
        space32.setPrev1Space(specialItem55); //space32.setPrev1Space(space31);
        space33.setNext1Space(junctionC); //space33.setNext1Space(space37); space33.setNext2Space(2);
        space33.setPrev1Space(junctionB); //space33.setPrev1Space(space52);
        space34.setNext1Space(space6);
        space34.setPrev1Space(space24);
        space35.setNext1Space(space36);
        space35.setPrev1Space(space2);
        space36.setNext1Space(space31);
        space36.setPrev1Space(space35);
        space37.setNext1Space(space38);
        space37.setPrev1Space(junctionC); //space37.setPrev1Space(space33);
        space38.setNext1Space(space39);
        space38.setPrev1Space(space37);
        space39.setNext1Space(space15);
        space39.setPrev1Space(space38);
        space39.setPrev2Space(space46);
        space40.setNext1Space(space41);
        space40.setPrev1Space(junctionD); //space40.setPrev1Space(space15);
        space41.setNext1Space(space42);
        space41.setPrev1Space(space40);
        space42.setNext1Space(space43);
        space42.setPrev1Space(space41);
        space43.setNext1Space(gateG); //space43.setNext1Space(space30); space43.setNext2Space(space44);
        space43.setPrev1Space(space42);
        space44.setNext1Space(space4);
        space44.setPrev1Space(gateG); //space44.setPrev1Space(space43);
        space45.setNext1Space(space11);
        space45.setPrev1Space(space6);
        space46.setNext1Space(space39);
        space46.setPrev1Space(space4);
        space47.setNext1Space(space9);
        space47.setPrev1Space(junctionB); //space47.setPrev1Space(space52);
        space48.setNext1Space(space48);
        space48.setPrev1Space(space3);
        space49.setNext1Space(space50);
        space49.setPrev1Space(space48);
        space49.setPrev2Space(space7);
        space50.setNext1Space(space20);
        space50.setPrev1Space(space49);
        space51.setNext1Space(specialItem56); //space51.setNext1Space(space7);
        space51.setPrev1Space(junctionE); //space51.setPrev1Space(space17);
        space52.setNext1Space(junctionB); //space52.setNext1Space(space47); space52.setNext2Space(33);
        space52.setPrev1Space(space19);
        start53.setNext1Space(space25);
        start54.setNext1Space(space37);
        specialItem55.setNext1Space(space32);
        specialItem55.setPrev1Space(space31);
        specialItem56.setNext1Space(space7);
        specialItem56.setPrev1Space(space51);
        junctionA.setNext1Space(space24);
        junctionA.setNext2Space(space27);
        junctionA.setPrev1Space(space26);
        junctionB.setNext1Space(space47);
        junctionB.setNext2Space(space33);
        junctionB.setPrev1Space(space52);
        junctionC.setNext1Space(space37);
        junctionC.setNext2Space(space2);
        junctionC.setPrev1Space(space33);
        junctionD.setNext1Space(space16);
        junctionD.setNext2Space(space40);
        junctionD.setPrev1Space(space15);
        junctionE.setNext1Space(space18);
        junctionE.setNext2Space(space51);
        junctionE.setPrev1Space(space17);
        gateF.setNext1Space(space29);
        gateF.setNext2Space(space3);
        gateF.setPrev1Space(space28);
        gateG.setNext1Space(space30);
        gateG.setNext2Space(space44);
        gateG.setPrev1Space(space43);
    }

}
