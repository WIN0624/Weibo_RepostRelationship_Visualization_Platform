package QDataType;
public class Query_content{
        protected String query;
	protected String page;
        public void set_page(String page){
                this.page=page;
        }
        public String get_page(){
                return this.page;
        }
        public void set_query(String query){
                this.query=query;
        }   
        public String get_query(){
                return this.query;
        }   
        @Override
        public String toString(){
                return "Query_content{query='"+query+"',"+"page='"+page+"'}";
        }
}
