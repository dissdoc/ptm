class User < ActiveRecord::Base
  has_many :accounts

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

  has_attached_file :avatar,
                    :styles => {:icon => "32x32>", :small => "64x64>", :medium => "260x180>" },
                    :storage => :Dropboxstorage,
                    :path => "/:attachment/:attachment/:id/:style/:filename"
  attr_accessible :avatar

  def self.search(query)
    where('first_name LIKE ? OR last_name LIKE ?', "%#{query}%", "%#{query}%")
  end

  def full_name
    [first_name, last_name].join(" ")
  end

  def apply_omniauth(omniauth)
    accounts.build(:provider => omniauth['provider'], :uid => omniauth['uid'])
  end

  def password_required?
    (accounts.empty? || !password.blank?) && super
  end

  def update_with_password(params = {})
    current_password = params.delete(:current_password) if !params[:current_password].blank?

    if params[:password].blank?
      params.delete(:password)
      params.delete(:password_confirmation) if params[:password_confirmation].blank?
    end

    result = if has_no_password? || valid_password?(current_password)
              update_attributes(params)
             else
              self.errors.add(:current_password, current_password.blank? ? :blank : :invalid)
              self.attributes = params
              false
             end

    clean_up_passwords
    result
  end

  def has_no_password?
    self.encrypted_password.blank?
  end
end
