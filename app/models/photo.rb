class Photo < ActiveRecord::Base
  belongs_to :album
  belongs_to :user

  has_many :taggings, :dependent => :destroy
  has_many :tags, :through => :taggings

  has_many :notes, :as => :notable, :dependent => :destroy

  has_one :geo, :dependent => :destroy
  has_one :share_photo, :dependent => :destroy

  has_many :photo_group_joins, :dependent => :destroy
  has_many :favorites, :dependent => :destroy

  has_attached_file :image,
                    :styles => {:icon => "64x64", :small => "100x63", :medium => "260x180", :large => "483x302" },
                    :storage => :Dropboxstorage,
                    :path => "/:attachment/:attachment/:id/:style/:filename"

  attr_accessible :album_id, :user_id, :comment, :image, :generate, :generate_end, :tag_names, :geo, :share_photo
  attr_writer :tag_names

  accepts_nested_attributes_for :geo, :reject_if => proc { |attrs| attrs.all? { |k, v| v.blank? } }
  attr_accessible :geo_attributes

  accepts_nested_attributes_for :share_photo
  attr_accessible :share_photo_attributes

  has_many :photo_assortment_joins, :dependent => :destroy

  has_many :recommend_geos, :dependent => :destroy

  after_save :assign_tags

  def tag_names
    @tag_names || tags.map(&:name).join(', ')
  end

  def self.get_by_datetime(datetime)
    where('generate <= ?', datetime)
  end

  def all_public
    share_photo
  end

  def admin_of?(current_user)
    user.id == current_user.id if current_user
  end

  def self.not_yet_added(items)
    if items.present?
      find(:all, :conditions => ['photo_id NOT IN (?)', items.map(&:id)])
    else
      all
    end
  end

  def can_add?(assortment)
    !photo_assortment_joins.where('assortment_id = ?', assortment.id).first.present?
  end

  private

    def assign_tags
      if @tag_names
        self.tags = @tag_names.split(/,\s+/).map do |name|
          Tag.find_or_create_by_name(name)
        end
      end
    end
end
