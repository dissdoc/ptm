class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :email, :password, :password_confirmation, :remember_me, :first_name, :last_name, :birthday

  validates :first_name, :last_name, :presence => true
  validates :first_name, :last_name, :length => { :maximum => 64 }

  has_many :albums, :dependent => :destroy
  has_many :photos, :dependent => :destroy
  has_many :notes, :dependent => :destroy
  has_many :activities, :dependent => :destroy

  has_many :friendlists, :dependent => :destroy

  has_many :friendships
  has_many :inverse_friendships, :class_name => "Friendship", :foreign_key => "friend_id"
  has_many :friends, :through => :friendships
  has_many :inverse_friends, :through => :inverse_friendships, :source => :user

  def self.search(query)
    where('first_name LIKE ? OR last_name LIKE ?', "%#{query}%", "%#{query}%")
  end

  def full_name
    [first_name, last_name].join(" ")
  end
end
