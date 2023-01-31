import java.sql.*;
import java.util.Scanner;

public class video_db {
    private static final String Super_ID = "svr123";
    private static final String Super_pwd = "svr456";
    static PreparedStatement pst = null;
    static Connection connect = null;
    static ResultSet rs = null;
    private static Scanner sc = new Scanner(System.in);

    private static String sql;
    private static String db_user;
    private static boolean db_exit;

    public static void DB_Connect() {
        Scanner keyboard = new Scanner(System.in);
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            //driver 가져오기
            String url = "jdbc:mysql://localhost:3306/db_project";
            System.out.print("Enter ID: ");
            String user = keyboard.next();

            System.out.print("Enter PW: ");
            String psw = keyboard.next();
            connect = DriverManager.getConnection( url, user, psw );
            //Connection 객체 생성 완료
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String getRandomString(int size) {
        if(size > 0) {
            char[] tmp = new char[size];
            for(int i=0; i<tmp.length; i++) {
                int div = (int) Math.floor( Math.random() * 2 );

                if(div == 0) {
                    tmp[i] = (char) (Math.random() * 10 + '0') ;
                }else {
                    tmp[i] = (char) (Math.random() * 26 + 'A') ;
                }
            }
            return new String(tmp);
        }
        return "사이즈를 입력하지 않았습니다.";
    }

    public static void SignUp() {
        try {
            System.out.println("---------- 회원가입 ----------");
            System.out.print("아이디(10자 이내): ");
            String new_ID = sc.next();
            System.out.print("비밀번호(12자 이내): ");
            String new_Pwd = sc.next();
            System.out.print("성(10자 이내): ");
            String new_Fname = sc.next();
            System.out.print("이름(10자 이내): ");
            String new_Lname = sc.next();
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            //TODO 중복 있는지 , 글자 수 넘는지 check.

            //사용자 데이터베이스 새로운 계정 집어넣기
            pst = connect.prepareStatement("insert into user values (?,?,?,?,?,?)");
            pst.setString(1, new_ID);
            pst.setString(2, new_Pwd);
            pst.setString(3, new_Fname);
            pst.setString(4, new_Lname);
            pst.setDate(5, sqlDate);
            pst.setString(6, Super_ID);
            pst.executeUpdate();
        }
        //인서트 완료
        catch (SQLException e) {
            System.out.println("회원가입 도중 오류가 발생하였습니다. 다시 진행해주시길 바랍니다.");
        }


    }

    public static int Login(Scanner s) throws SQLException {
        System.out.println("---------- Login ----------");
        System.out.print("ID: ");
        String Login_ID = s.next();
        System.out.print("Password: ");
        String Login_Pwd = s.next();

        sql = "SELECT Password FROM USER WHERE userID = ?";

        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, Login_ID);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).contentEquals(Login_Pwd)) {
                    System.out.println("로그인에 성공하였습니다!");
                    db_user = Login_ID;
                    return 1;
                } else {
                    System.out.println("비밀번호가 다릅니다. 다시 시도해주세요.");
                    return 0;
                }
            }
            System.out.println("아이디가 다릅니다. 다시 시도해주세요.");
            return -1;
        }

        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //내 동영상 보여주는 함수
    public static void showMyVideo(){
        System.out.println("---------- 동영상 목록 ----------");
        System.out.println("현재 사용자가 업로드한 동영상 목록을 출력합니다.");
        sql = "select title,videonum from (select * from upload_list join video where ul_num = videonum)V where ul_ID = ?";
        int num = 0;
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            while(rs.next()){
                num++;
                System.out.println(num + ". "+ rs.getString(1) +" " + rs.getString(2));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void upload(){
        Scanner sc = new Scanner(System.in);
        //업로드할 영상의 고유번호는 중복 없이 랜덤번호로 설정된다.
        System.out.println("업로드할 동영상의 제목을 입력하세요.(30자 이내)");
        String title = sc.nextLine();
        System.out.println("동영상의 주제를 입력하세요.(10자 이내)");
        String topic = sc.nextLine();
        System.out.println("동영상에 대한 설명을 입력하세요.(500자 이내)");
        String description = sc.nextLine();

        String num = getRandomString(5);
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        try {
            pst = connect.prepareStatement("insert into video values (?,?,?,?)");
            //사용자의 영상 업로드
            pst.setString(1, topic);
            pst.setString(2, description);
            pst.setString(3, num);
            pst.setString(4, title);
            pst.executeUpdate();
            System.out.println("동영상 업로드를 완료하였습니다.");
            
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            pst = connect.prepareStatement("insert into upload_list values (?,?,?)");
            pst.setString(1, num);
            pst.setString(2, db_user);
            pst.setDate(3, sqlDate);
            pst.executeUpdate();
        }
         catch(SQLException e) {
             throw new RuntimeException(e);
         }

    }

    public static void video_delete(){
        System.out.println("---------- 동영상 삭제 ----------");
        System.out.println("현재 사용자가 업로드한 동영상 목록을 출력합니다.");
        String sql = "select title,videonum from (select * from upload_list join video where ul_num = videonum)V where ul_ID = ?";
        int num = 0;
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            while(rs.next()){
                num++;
                System.out.println("동영상 ["+num +"] "+ rs.getString(1) +" " + rs.getString(2));
            }

            if (num == 0){
                System.out.println("업로드한 영상이 없습니다. 메뉴화면으로 돌아갑니다.");
                return;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("삭제할 동영상의 고유번호를 입력해주세요.");
        String v_num = sc.nextLine();
        try {
            pst = connect.prepareStatement("delete from video where videoNum = ?");
            pst.setString(1, v_num);
            pst.executeUpdate();
            System.out.println("동영상 삭제가 완료되었습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void user_change(){
        System.out.println("---------- 동영상 수정 ----------");
        System.out.println("현재 사용자가 업로드한 동영상 목록을 출력합니다.");
        String sql = "select title,videonum from (select * from upload_list join video where ul_num = videonum)V where ul_ID = ?";
        int num = 0;
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            while(rs.next()){
                num++;
                System.out.println("동영상 ["+num +"] "+ rs.getString(1) +" " + rs.getString(2));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("수정할 동영상의 고유번호를 입력해주세요.");
        String v_num = sc.nextLine();
        System.out.println("무엇을 수정하시겠습니까?");
        System.out.println("1.제목     2.주제     3.설명 ");
        int menu = sc.nextInt();
        sc.nextLine();

        switch (menu){
            case 1:
                System.out.println("변경할 제목을 입력하세요.");
                String new_title = sc.nextLine();
                try {
                    pst = connect.prepareStatement("update video set title = ? where videonum = ?");
                    pst.setString(1, new_title);
                    pst.setString(2, v_num);
                    pst.executeUpdate();
                    break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            case 2:
                System.out.println("변경할 주제를 입력하세요.");
                String new_topic= sc.nextLine();
                try {
                    pst = connect.prepareStatement("update video set topic = ? where videonum = ?");
                    pst.setString(1, new_topic);
                    pst.setString(2, v_num);
                    pst.executeUpdate();
                    break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            case 3:
                System.out.println("변경할 설명을 입력하세요.");
                String new_des = sc.nextLine();
                try {
                    pst = connect.prepareStatement("update video set description = ? where videonum = ?");
                    pst.setString(1, new_des);
                    pst.setString(2, v_num);
                    rs = pst.executeQuery();
                    break;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }
        System.out.println("수정이 완료되었습니다.");
    }
    
    public static void video_search(){
        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.println("---------- 동영상 찾기 ----------");
            System.out.println("찾으시는 동영상과 관련된 키워드를 입력하세요. 해당 키워드를 포함하는 동영상을 나열합니다.");
            String keyword = sc.nextLine();

            sql = "SELECT title, videoNum FROM video WHERE title Like ?";
            int num = 0;
            try {
                pst = connect.prepareStatement(sql);
                pst.setString(1, "%"+keyword+"%");
                rs = pst.executeQuery();

                while(rs.next()){
                    num++;
                    System.out.println("결과 ["+num +"] "+ rs.getString(1) +" " + rs.getString(2));
                }
                if (num == 0) {
                    System.out.println("해당 키워드를 포함하는 동영상이 존재하지 않습니다.");
                    return;
                }
                else{
                    System.out.println("동영상 검색을 완료하였습니다.");
                    System.out.println("동영상 재생을 원하시면 동영상의 번호를, 그렇지 않으면 '0'을 눌려주세요.");
                    while(true){
                    String func = sc.nextLine();
                    if(func.equals('0')){
                        System.out.println("메뉴화면으로 돌아갑니다.");
                        return;
                    }
                    else {
                        sql = "select count(*) from video where videonum = ?";
                        try {
                            pst = connect.prepareStatement(sql);
                            pst.setString(1, func);
                            rs = pst.executeQuery();
                            while (rs.next()) {
                                if (rs.getInt(1) == 0) {
                                    System.out.println("동영상의 고유번호를 잘못 입력하셨습니다. 다시 시도해주세요.");
                                } else {
                                    play_Video(func);
                                    return; // 재생 후 기능을 이용 후에는 메뉴화면으로 갑니다.
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void play_Video (String num) {
            System.out.println("재생을 시작합니다.");
            System.out.println();
            System.out.println("@@@@@@@@@@" + num+ " 재생중 @@@@@@@@@@");
            System.out.println();
            sql = "select count(*) from history_list where hs_ID = ?";
            try {
                pst = connect.prepareStatement(sql);
                pst.setString(1, db_user);
                rs = pst.executeQuery();
                while (rs.next()) {
                    if (rs.getInt(1) == 0) { //시청기록에 아이디가 없는 경우 추가
                        sql = "insert into history_list values (?)";
                        pst = connect.prepareStatement(sql);
                        pst.setString(1, db_user);
                        pst.executeUpdate();
                    } // 시청기록에 아이디가 있는 경우
                    sql = "select count(*) from view_video where view_ID = ? and view_num = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, db_user);
                    pst.setString(2, num);
                    rs = pst.executeQuery();
                    java.util.Date utilDate = new java.util.Date();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                    while (rs.next()){
                        if(rs.getInt(1) == 0){
                            pst = connect.prepareStatement("insert into view_video values (?,?,?)");
                            pst.setString(1, db_user);
                            pst.setString(2, num);
                            pst.setDate(3, sqlDate);
                            pst.executeUpdate();
                        }
                        else{
                            pst = connect.prepareStatement("update view_video set view_Date = ? where view_ID = ? and view_num = ?");
                            pst.setDate(1, sqlDate);
                            pst.setString(2, db_user);
                            pst.setString(3, num);
                            pst.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            while(true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("원하는 기능을 입력해주세요.");
                System.out.println("1. 좋아요    2. 좋아요 취소   3. 게시자 구독하기   4. 게시자 구독 취소  5. 재생 목록에 추가   6. 재생 종료");
                int func = sc.nextInt();
                switch (func){
                    case 1 : like_video(num); break;
                    case 2 : dislike_video(num); break;
                    case 3 : sql = "select ul_ID from upload_list where ul_num = ?";
                            try {
                                pst = connect.prepareStatement(sql);
                                pst.setString(1, num);
                                rs = pst.executeQuery();
                                String n_ID;
                                while(rs.next()) {
                                    n_ID = rs.getString("ul_ID");
                                    System.out.println("해당 동영상의 게시자는 " + n_ID + "입니다.");
                                    subscribe_(n_ID);

                                }break;
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                    case 4 :
                        sql = "select ul_ID from upload_list where ul_num = ?";
                        try {
                            pst = connect.prepareStatement(sql);
                            pst.setString(1, num);
                            rs = pst.executeQuery();
                            String n_ID;
                            while(rs.next()) {
                                n_ID = rs.getString("ul_ID");
                                System.out.println("해당 동영상의 게시자는 " + n_ID + "입니다.");
                                cancel_subscribe(n_ID);
                                                            }
                            break;
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    case 5 :
                        System.out.println("추가할 재생 목록을 찾습니다.");
                        print_playlist();
                        sc.nextLine();
                        System.out.println("추가할 재생목록의 제목을 입력하세요. 종료를 원하시면 '0'을 입력하세요.");
                        String pl_title = sc.nextLine();
                        if (pl_title.equals('0')){
                            return;
                        }
                        try {
                            sql = "insert into add_video values (?,?,?)";
                            pst = connect.prepareStatement(sql);
                            pst.setString(1,db_user);
                            pst.setString(2,num);
                            pst.setString(3,pl_title);
                            pst.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("재생목록이 생성되었습니다.");
                }
                break;
            }
        }

        public static void like_video(String num) {
            sql = "select count(*) from video_like where lk_ID = ? and lk_num = ? ";
                try {
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, db_user);
                    pst.setString(2, num);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        if (rs.getInt(1) == 0) {
                            sql = "insert into video_like values (?,?)";
                            try {
                                pst = connect.prepareStatement(sql);
                                pst.setString(1, db_user);
                                pst.setString(2, num);
                                pst.executeUpdate();
                                System.out.println("좋아요를 하셨습니다.");
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            System.out.println("이미 좋아요를 하셨습니다.");
                            return;
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        }

    public static void dislike_video(String num) {

        try {
            sql = "select count(*) from video_like where lk_ID = ? and lk_num = ? ";
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            pst.setString(2, num);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("좋아요를 누르지 않으셨습니다.");
                }
                else {
                    sql = "delete from video_like where lk_ID = ? and lk_num = ?";
                    try {
                        pst = connect.prepareStatement(sql);
                        pst.setString(1, db_user);
                        pst.setString(2, num);
                        pst.executeUpdate();
                        System.out.println("좋아요를 취소하셨습니다.");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void subscribe_(String ID) {
        if (ID.equals(db_user)){
            System.out.println("자기 자신은 구독할 수 없습니다.");
            return;
        }
        try {
            sql = "insert into subscribe values (?,?)";
            pst = connect.prepareStatement(sql);
            pst.setString(1, ID); //구독 되는 사람
            pst.setString(2, db_user); // 구독 하는 사람
            pst.executeUpdate();
            System.out.println("구독을 완료하였습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cancel_subscribe(String ID) {
        if (ID.equals(db_user)){
            System.out.println("자기 자신은 구독할 수 없습니다.");
            return;
        }
        try {
            sql = "delete from subscribe where sub_ID = ? and subscriber = ?";
            pst = connect.prepareStatement(sql);
            pst.setString(1, ID); //구독 되는 사람
            pst.setString(2, db_user); // 구독 하는 사람
            pst.executeUpdate();
            System.out.println("구독을 취소하였습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void print_playlist() {
        sql = "select count(*) from playlist where pl_ID = ? ";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            int num = 0;
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("소유하고 있는 플레이리스트가 없습니다.");
                }
                else {
                    sql = "select pl_title from playlist where pl_ID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, db_user);
                    try {
                        rs = pst.executeQuery();
                        while (rs.next()) {
                            num++;
                            System.out.println("재생목록 ["+num +"] "+ rs.getString(1));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void user_playlist() {
        print_playlist();
        try {
            System.out.println("원하는 기능을 입력해주세요.");
            System.out.println("1. 재생목록 만들기 2. 재생목록 삭제 3. 재생목록 선택 4. 메뉴로 돌아가기");
            int func_ = sc.nextInt();
            sc.nextLine();
            switch (func_) {
                case 1: //재생목록 만들기
                    System.out.println("만들 재생목록의 제목을 입력해주세요.");
                    String pl_ttl = sc.nextLine();
                    try {

                        sql = "insert into playlist values (?,?)";
                        pst = connect.prepareStatement(sql);
                        pst.setString(1, db_user);
                        pst.setString(2, pl_ttl);
                        pst.executeUpdate();
                        System.out.println("재생목록을 생성하였습니다.");
                        System.out.println();
                        return;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }


                case 2:
                    System.out.println("삭제할 재생목록의 제목을 입력해주세요.");
                    String pl_ttl2 = sc.nextLine();
                    sql = "delete from playlist where pl_title = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, pl_ttl2);
                    pst.executeUpdate();
                    System.out.println("재생목록을 삭제하였습니다.");
                    System.out.println();
                    return;

                case 3:

                    System.out.println("원하는 재생 목록을 선택해주세요.");
                    String title = sc.nextLine();
                    sql = "select title from video where videonum = (select add_num from add_video where add_Title = ?)";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, title);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                            num++;
                            System.out.println("동영상 ["+num +"] "+ rs.getString(1));
                    }
                    if(num == 0){
                        System.out.println("재생목록 내에 추가된 영상이 없습니다.");
                        return;
                    }

                    System.out.println();
                    System.out.println("------------------------------");
                    System.out.println("1. 전체 재생 2. 특정 영상 재생 3. 특정 영상 삭제 4. 메뉴로 돌아가기");
                    int func = sc.nextInt();

                    switch (func) {
                        case 1:

                            sql = "select add_num from add_video where add_title = ?";
                            pst = connect.prepareStatement(sql);
                            pst.setString(1, title);
                            rs = pst.executeQuery();
                            while (rs.next()) {
                                play_Video(rs.getString(1));
                            }
                            return;

                        case 2:

                            System.out.println("재생할 동영상의 번호를 입력해주세요.");
                            String numb = sc.nextLine();
                            play_Video(numb);
                            return;

                        case 3:
                            System.out.println("삭제할 동영상의 번호를 입력해주세요.");
                            sc.nextLine();
                            String numb2 = sc.nextLine();
                            sql = "delete from add_video where add_ID = ? and add_num = ? and add_Title = ? ";
                            pst = connect.prepareStatement(sql);
                            pst.setString(1,db_user);
                            pst.setString(2, numb2);
                            pst.setString(3, title);
                            pst.executeUpdate();
                            System.out.println("삭제를 완료하였습니다.");
                            return;


                        case 4:
                            return;

                    }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void user_sublist(){
        sql = "select count(*) from subscribe where subscriber = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("구독중인 게시자가 없습니다.");
                    return;
                } else {
                    sql = "select sub_ID from subscribe where subscriber = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, db_user);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1));
                    }
                }
                            }
            System.out.println("원하는 기능을 입력해주세요.");
            System.out.println("1. 게시자 구독 취소    2. 메뉴 화면으로 돌아가기");
            int func = sc.nextInt();
            sc.nextLine();
            if (func == 1){
                System.out.println("구독 취소를 원하는 게시자의 ID를 입력하세요.");
                String cancel_ID = sc.nextLine();
                cancel_subscribe(cancel_ID);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void user_history(){
        sql = "select count(*) from view_video where view_ID = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("시청했던 동영상이 없습니다.");
                } else {
                    sql = "select title,videonum from (select * from view_video join video where view_num = videonum)V where view_ID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, db_user);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1) + " " + rs.getString(2));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showUserInf(){
        if (db_user.equals("svr123")){
            sql = "select * from supervisor";
            try {
                pst = connect.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    System.out.print( "아이디: "+ rs.getString(1) + "\n" +
                            "비밀번호: " + rs.getString(2) + "\n"
                    );
                }
                System.out.println();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        sql = "select * from user where userID = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println( "아이디: "+ rs.getString(1) + "\n" +
                        "비밀번호: " + rs.getString(2) + "\n" +
                        "성: " + rs.getString(3) + "\n" +
                        "이름: " + rs.getString(4) + "\n" +
                        "가입일 : " + rs.getString(5)
                );
            }
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void User_Menu(){
        while(true){
            System.out.println("========= 사용자 메뉴 ==========");
            System.out.println("0. 내 정보 출력");
            System.out.println("1. 내 동영상 목록");
            System.out.println("2. 동영상 업로드"); //비디오 등록하는 것
            System.out.println("3. 동영상 수정"); //자신이 업로드한 비디오 수정
            System.out.println("4. 동영상 삭제"); //자신이 업로드한 비디오 삭제
            System.out.println("5. 동영상 찾기"); //비디오 탐색
            System.out.println("6. 재생 목록");
            System.out.println("7. 구독 목록");
            System.out.println("8. 시청 기록");
            System.out.println("9. 로그아웃");
            int menu = sc.nextInt();
            switch (menu){
                case 0:
                    showUserInf();
                    continue;
                case 1:
                    showMyVideo();
                    continue;
                case 2:
                    upload();
                    continue;
                case 3:
                    user_change();
                    continue;
                case 4:
                    video_delete();
                    continue;
                case 5:
                    video_search();
                    continue;
                case 6:
                    user_playlist();
                    continue;
                case 7:
                    // 구독목록
                    user_sublist();
                    continue;

                case 8: // 시청기록
                    user_history();
                    continue;

                case 9: return;
            }
        }
    }

    public static int supervisor_login(){
        System.out.println("---------- 관리자 로그인 ----------");
        System.out.print("아이디: ");
        db_user = sc.next();
        System.out.print("비밀번호: ");
        String Login_Pwd = sc.next();

        sql = "SELECT Password FROM supervisor WHERE super_ID = ?";

        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, db_user);
            rs = pst.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).contentEquals(Login_Pwd)) {
                    System.out.println("로그인에 성공하였습니다!");
                    System.out.println();
                    return 1;
                } else {
                    System.out.println("비밀번호가 다릅니다. 다시 시도해주세요.");
                    return 0;
                }
            }
            System.out.println("아이디가 다릅니다. 다시 시도해주세요.");
            return -1;
        }

        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void supervisor_Menu(){
        while(true){
            System.out.println("========= 관리자 메뉴 ==========");
            System.out.println("0. 관리자 정보 출력");
            System.out.println("1. 유저 목록");
            System.out.println("2. 시청 기록 열람"); //비디오 등록하는 것
            System.out.println("3. 업로드 목록 열람");
            System.out.println("4. 로그아웃");
            int menu = sc.nextInt();
            switch (menu){
                case 0:
                    showUserInf();
                    continue;
                case 1:
                    showUsers();
                    continue;
                case 2:
                    svr_history();
                    continue;
                case 3:
                    svr_uploadlist();
                    continue;
                case 4:
                    return;
            }
        }
    }

    public static void showUsers(){
        System.out.println("모든 유저의 정보들을 출력합니다.");
        sql = "select count(*) from user";
        try {
            pst = connect.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("회원가입한 유저가 없습니다.");
                    return;
                } else {
                    sql = "select userID from user where superID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, Super_ID);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        System.out.println("유저 삭제를 원하시면 1, 종료를 원하면 0을 입력하세요.");
        int num = sc.nextInt();
        sc.nextLine();
         if (num == 1){
            delete_user();
        }
    }
    
    public static void delete_user(){
        System.out.println("모든 유저의 정보들을 출력합니다.");
        sql = "select count(*) from user";
        try {
            pst = connect.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("회원가입한 유저가 없습니다.");
                    return;
                } else {
                    sql = "select userID from user where superID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, Super_ID);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("삭제할 유저의 아이디를 입력하세요. 종료를 원하시면 '0'을 입력하세요.");
        String u_id = sc.nextLine();
        if (u_id.equals('0'))
        {
            return;
        }
        try {
            pst = connect.prepareStatement("delete from user where userID = ?");
            pst.setString(1, u_id);
            pst.executeUpdate();
            System.out.println("유저 삭제가 완료되었습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void svr_history(){
        System.out.println("모든 유저의 정보들을 출력합니다.");
        sql = "select count(*) from user";
        try {
            pst = connect.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("회원가입한 유저가 없습니다.");
                    return;
                } else {
                    sql = "select userID from user where superID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, Super_ID);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sc.nextLine();
        System.out.println("시청기록을 열람할 유저의 아이디를 입력하세요. 종료를 원하시면 '0'을 입력하세요.");
        String u_id = sc.nextLine();
        if (u_id.equals('0'))
        {
            return;
        }
        sql = "select count(*) from view_video where view_ID = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, u_id);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("시청했던 동영상이 없습니다.");
                    return;
                } else {
                    sql = "select view_Num, view_Date from view_video where view_ID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, u_id);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1) + " " + rs.getString(2));
                    }
                }
            }
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("원하는 기능을 입력하세요.");
        System.out.println("1. 시청 기록 삭제     2. 종료");
        int his_del = sc.nextInt();

        if (his_del == 1){
           sv_deletehis(u_id);
            return;
        }
        else{
            System.out.println("관리자 메뉴로 돌아갑니다.");
            return;
        }
    }

    public static void sv_deletehis(String id){
        System.out.println("삭제를 원하는 동영상의 고유번호를 입력하세요.");
        sc.nextLine();
        String v_num = sc.nextLine();
        sql = "delete from view_video where view_ID = ? and view_num = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, v_num);
            pst.executeUpdate();
            System.out.println("해당 유저의 동영상 시청기록의 삭제를 완료하였습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void svr_uploadlist(){
        System.out.println("모든 유저의 정보들을 출력합니다.");
        sql = "select count(*) from user";
        try {
            pst = connect.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("회원가입한 유저가 없습니다.");
                    return;
                } else {
                    sql = "select userID from user where superID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, Super_ID);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sc.nextLine();
        System.out.println("업로드 목록을 열람할 유저의 아이디를 입력하세요. 종료를 원하시면 '0'을 입력하세요.");
        String u_id = sc.nextLine();
        if (u_id.equals('0'))
        {
            return;
        }
        sql = "select count(*) from upload_list where ul_ID = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, u_id);
            rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    System.out.println("업로드한 동영상이 없습니다.");
                    return;
                } else {
                    sql = "select title,videonum, ul_Date from (select * from upload_list join video where ul_num = videonum)V where ul_ID = ?";
                    pst = connect.prepareStatement(sql);
                    pst.setString(1, u_id);
                    rs = pst.executeQuery();
                    int num = 0;
                    while (rs.next()) {
                        num++;
                        System.out.println("["+num +"] "+ rs.getString(1) + " " + rs.getString(2) + " " + rs.getString(3));
                    }
                }
            }
            System.out.println();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("원하는 기능을 입력하세요.");
        System.out.println("1. 동영상 삭제    2. 종료");
        int del_num = sc.nextInt();
        sc.nextLine();
        if(del_num==1){
            sv_deleteupl(u_id);
        }
        else{
            return;
        }
    }

    public static void sv_deleteupl(String id){
        System.out.println("삭제를 원하는 동영상의 고유번호를 입력하세요.");
        String v_num = sc.nextLine();
        sql = "delete from  upload_list where ul_ID = ? and ul_num = ?";
        try {
            pst = connect.prepareStatement(sql);
            pst.setString(1, id);
            pst.setString(2, v_num);
            pst.executeUpdate();
            System.out.println("해당 유저의 업로드된 동영상을 삭제를 완료하였습니다.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException{
        DB_Connect(); //DB 연결하기
        db_exit = false;
        Scanner sc = new Scanner(System.in);
        while(true) {
            if (db_exit) break;
            System.out.println("========== Video Site ==========");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 관리자 로그인");
            System.out.println("4. 종료");
            System.out.println("원하는 항목을 고르십시오.");
            int num = sc.nextInt();
            if (num == 1) {
                SignUp();
            } else if (num == 2) {
                switch (Login(sc)) {
                    case 1: //로그인 성공. 메뉴 화면으로 감
                        User_Menu();
                        continue;
                    case 0: //비밀번호 다름.
                        continue;
                    case -1:
                        //아이디 다름
                        continue;
                    case -2: //DB 종료

                        System.exit(1);
                }
            }
            else if (num == 3){
                switch (supervisor_login()) {
                    case 1: //로그인 성공. 메뉴 화면으로 감
                        supervisor_Menu();
                        continue;
                    case 0: //비밀번호 다름.
                        continue;
                    case -1:
                        //아이디 다름
                        continue;
                    case -2: //DB 종료
                        System.exit(1);
                }
            }
            else if (num == 4){
                break;

            }
            
        }
        if (rs != null) rs.close();
        if (pst != null) pst.close();
        if (!connect.isClosed()) connect.close();

    }
}

