class Photo < ActiveRecord::Base
  belongs_to :album

  has_many :taggings, :dependent => :destroy
  has_many :tags, :through => :taggings

  has_attached_file :image,
                    :styles => {:icon => "64x64>", :small => "100x63>", :medium => "260x180>", :large => "483x302>" }

  attr_accessible :album_id, :image, :generate, :tag_names

  after_save :assign_tags

  def tag_names
    @tag_names || tags.map(&:name).join(' ')
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
