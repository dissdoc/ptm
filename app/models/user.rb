class User < ActiveRecord::Base
  has_many :accounts

  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :recoverable, :rememberable, :trackable, :validatable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :email, :password, :password_confirmation,
                  :remember_me, :first_name, :last_name, :birthday,
                  :about, :current_city, :gender

  validates :first_name, :last_name, :presence => true
  validates :first_name, :last_name, :length => { :maximum => 64 }

  has_many :albums, :dependent => :destroy
  has_many :photos, :dependent => :destroy
  has_many :notes, :dependent => :destroy
  has_many :activities, :dependent => :destroy

  has_many :friendships, :dependent => :destroy

  has_many :inverse_friendships,
      :class_name => "Friendship",
      :foreign_key => "friend_id"

  has_many :friends,
      :through => :friendships

  has_many :inverse_friends,
      :through => :inverse_friendships,
      :source => :user

  has_many :messages,
      :foreign_key => "to_user_id",
      :dependent => :destroy

  has_many :to_me,
      :class_name => 'Message',
      :foreign_key => "to_user_id"

  has_many :from_me,
      :class_name => 'Message',
      :foreign_key => "from_user_id"

  has_many :from_me,
      :through => :messages,
      :class_name => 'Message',
      :foreign_key => 'from_user_id',
      :source => :from_user

  has_attached_file :avatar,
                    :styles => {:icon => "32x32>", :small => "64x64>", :medium => "260x180>" },
                    :storage => :Dropboxstorage,
                    :path => "/:attachment/:attachment/:id/:style/:filename"
  attr_accessible :avatar

  has_many :group_joins, :dependent => :destroy

  has_many :managing_groups,
      :through => :group_joins,
      :class_name => 'Group',
      :source => :group,
      :conditions => ['group_joins.role = ?', 'admin']

  has_many :contained_groups,
      :through => :group_joins,
      :class_name => 'Group',
      :source => :group,
      :conditions => ['group_joins.role = ? OR group_joins.role = ?', 'member', 'admin']

  has_many :joining_groups,
      :through => :group_joins,
      :class_name => 'Group',
      :source => :group,
      :conditions => ['group_joins.role = ? AND group_joins.accepted = ? AND group_joins.agree = ?', 'member', true, true]

  has_many :requesting_groups,
      :through => :group_joins,
      :class_name => 'Group',
      :source => :group,
      :conditions => ['group_joins.role = ? AND group_joins.accepted = ? AND group_joins.agree = ?', 'member', false, true]

  has_many :inviting_groups,
      :through => :group_joins,
      :class_name => 'Group',
      :source => :group,
      :conditions => ['group_joins.role = ? AND group_joins.accepted = ? AND group_joins.agree = ?', 'member', true, false]

  has_many :favorites, :order => 'created_at desc', :dependent => :destroy

  has_many :current_favorites,
      :through => :favorites,
      :class_name => 'Photo',
      :source => :photo,
      :order => 'created_at desc'

  has_many :dashboards, :dependent => :destroy

  has_many :familytrees, :dependent => :destroy

  has_many :assortments, :dependent => :destroy

  has_many :taggings, :dependent => :destroy
  has_many :tags, :through => :taggings

  has_many :recommend_geos, :dependent => :destroy
  has_many :recommend_ats, :dependent => :destroy

  has_many :areatags, :dependent => :destroy

  def self.search(query)
    where('first_name LIKE ? OR last_name LIKE ?', "%#{query}%", "%#{query}%")
  end

  def self.free_all(user)
    if user
      where('id != ?', user.id)
    else
      all
    end
  end

  def full_name
    [first_name, last_name].join(" ")
  end

  def collections
    albums.where(:collection => true)
  end

  def apply_omniauth(omniauth)
    accounts.build(:provider => omniauth['provider'], :uid => omniauth['uid'])
  end

  def password_required?
    (accounts.empty? || !password.blank?) && super
  end

  def update_with_password(params = {})
    if params[:password].blank?
      params.delete(:password)
      params.delete(:password_confirmation) if
          params[:password_confirmation].blank?
    end

    update_attributes(params)
  end

  def has_no_password?
    self.encrypted_password.blank?
  end

  def manager?
    managing_groups.present?
  end

  def admin_of?(group)
    managing_groups.where(:id => group.id).first.present?
  end

  def join_of?(group)
    joining_groups.where(:id => group.id).first.present?
  end

  def invite_of?(group)
    inviting_groups.where(:id => group.id).first.present?
  end

  def can_join?(group)
    group_joins.where(:group_id => group.id).first.blank?
  end

  def admin_of_album?(album)
    albums.where(:id => album.id).first.present?
  end

  def admin_of_photo?(photo)
    photos.where(:id => photo.id).first.present?
  end

  def admin_of_tag?(photo, tag)
    taggings.where(:tag_id => tag.id, :photo_id => photo.id).first.present?
    end

  def admin_of_areatag?(photo, areatag)
    areatags.where(:id => areatag.id, :photo_id => photo.id).first.present?
  end

  def admin_of_assortment(assort)
    assortments.where(:id => assort.id).first.present?
  end

  def photo_fave?(photo)
    favorites.where('photo_id = ?', photo.id).first.present?
  end
end