package bean
{
	[RemoteClass(alias="com.pmprental.bean.FamiliaBean")]
	public class FamiliaBean
	{
		private var _id:Number;
		private var _descricao:String;
		
		public function get id(): Number{return _id};
		public function set id(id: Number): void{this._id = id}; 

		public function get descricao(): String{return _descricao};
		public function set descricao(descricao: String): void{this._descricao = descricao}; 
		
		public function FamiliaBean()
		{
		}
	}
}