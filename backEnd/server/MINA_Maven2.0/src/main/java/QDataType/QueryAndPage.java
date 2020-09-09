package QDataType;
public class QueryAndPage extends Query_content{
        protected String page;
        public void set_page(String page){
                this.page=page;
        }
        public String get_page(){
                return this.page;
        }
        @Override
        public String toString(){
                return "Query_conten{query='"+query+"'"+"page='"+page+"'}";
        }    
}

