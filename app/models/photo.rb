class Photo < ActiveRecord::Base
  belongs_to :album
  belongs_to :user

  has_many :taggings, :dependent => :destroy
  has_many :tags, :through => :taggings
  has_many :notes, :dependent => :destroy

  has_one :geo, :dependent => :destroy
  has_one :share_photo, :dependent => :destroy

  has_attached_file :image,
                    :styles => {:icon => "64x64>", :small => "100x63>", :medium => "260x180>", :large => "483x302>" }

  attr_accessible :album_id, :user_id, :image, :generate, :tag_names, :geo, :share_photo
  attr_writer :tag_names

  accepts_nested_attributes_for :geo, :reject_if => proc { |attrs| attrs.all? { |k, v| v.blank? } }
  attr_accessible :geo_attributes

  accepts_nested_attributes_for :share_photo
  attr_accessible :share_photo_attributes

  after_save :assign_tags

  def tag_names
    @tag_names || tags.map(&:name).join(' ')
  end

  def self.get_by_datetime(datetime)
    where('generate <= ?', datetime)
  end

  def all_public
    share_photo
  end

  private

    def assign_tags
      if @tag_names
        self.tags = @tag_names.split(/\s+/).map do |name|
          Tag.find_or_create_by_name(name)
        end
      end
    end
end
