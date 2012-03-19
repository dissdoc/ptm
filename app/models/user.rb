class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :email, :password, :password_confirmation, :remember_me, :first_name, :last_name, :birthday

  validates :first_name, :last_name, :presence => true
  validates :first_name, :last_name, :length => { :maximum => 64 }

  has_many :albums

  def full_name
    [first_name, last_name].join(" ")
  end
end
