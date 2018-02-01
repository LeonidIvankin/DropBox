package server;

import java.util.ArrayList;

public class BaseAuthService implements AuthService{
    private class Entry{
        private String login;
        private String pass;
        private String nick;
        public Entry(String login, String pass, String nick){
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
    private ArrayList<Entry> entries;
    @Override
    public void start(){
    }
    @Override
    public void stop(){
    }
    public BaseAuthService(){
        entries = new ArrayList<>();
        entries.add(new Entry("l1", "p1", "leo"));
        entries.add(new Entry("l2", "p2", "alex"));
        entries.add(new Entry("l3", "p3", "n3"));
    }
    @Override
    public String getNickByLoginPass(String login, String pass){
        for(Entry e: entries){
            if(e.login.equals(login) && e.pass.equals(pass)) return e.nick;
        }
        return null;
    }
}
